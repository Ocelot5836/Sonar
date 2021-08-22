package io.github.ocelot.sonar.client.shader;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.resource.SelectiveReloadStateHandler;
import net.minecraftforge.resource.VanillaResourceType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20C;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20C.*;

/**
 * <p>Loads GLSL shaders designed for rendering instead of Post-Processing.</p>
 *
 * @author Ocelot
 */
public final class ShaderLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ShaderProgram.Shader, Map<ResourceLocation, Integer>> SHADERS = new HashMap<>();
    private static final Map<ResourceLocation, ShaderProgram> PROGRAMS = new HashMap<>();
    private static final Map<ShaderInstance, ResourceLocation> INSTANCES = new HashMap<>();

    private ShaderLoader()
    {
    }

@ApiStatus.Internal
    public static void init(IEventBus bus)
    {
        ShaderConst.init(bus);
        bus.addListener(EventPriority.NORMAL, false, ColorHandlerEvent.Block.class, event ->
        {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            if (resourceManager instanceof ReloadableResourceManager)
            {
                ((ReloadableResourceManager) resourceManager).registerReloadListener(new Reloader());
            }
        });
    }

    /**
     * Creates a new {@link ShaderInstance} of the specified type.
     *
     * @param program The program to create
     * @return A new shader ready to use
     */
    public static ShaderInstance create(ResourceLocation program)
    {
        try
        {
            int programId = linkShaders(program, 0);
            ShaderInstance instance = new ShaderInstance(programId);
            INSTANCES.put(instance, program);
            return instance;
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to create new shader instance: " + program, e);
            ShaderInstance instance = new ShaderInstance(-1);
            INSTANCES.put(instance, program);
            return instance;
        }
    }

    private static OptionalInt getShader(ShaderProgram.Shader type, ResourceLocation id)
    {
        if (!SHADERS.containsKey(type))
            return OptionalInt.empty();
        Map<ResourceLocation, Integer> map = SHADERS.get(type);
        return map.containsKey(id) ? OptionalInt.of(map.get(id)) : OptionalInt.empty();
    }

    private static int loadShader(CharSequence data, ShaderProgram.Shader type) throws ShaderException
    {
        int shader = glCreateShader(type.getGLType());
        glShaderSource(shader, data);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
            throw new ShaderException(glGetShaderInfoLog(shader, 512));
        return shader;
    }

    private static int linkShaders(ResourceLocation program, int programId) throws ShaderException
    {
        if (!PROGRAMS.containsKey(program))
            throw new IllegalStateException("Unknown program: " + program);

        ShaderProgram p = PROGRAMS.get(program);
        OptionalInt vertex = p.getVertexShader().map(shader -> getShader(ShaderProgram.Shader.VERTEX, shader)).orElse(OptionalInt.empty());
        OptionalInt fragment = p.getFragmentShader().map(shader -> getShader(ShaderProgram.Shader.FRAGMENT, shader)).orElse(OptionalInt.empty());
        OptionalInt geometry = p.getGeometryShader().map(shader -> getShader(ShaderProgram.Shader.GEOMETRY, shader)).orElse(OptionalInt.empty());
        OptionalInt[] compute = p.getComputeShaders().map(array -> Stream.of(array).map(shader -> getShader(ShaderProgram.Shader.COMPUTE, shader)).toArray(OptionalInt[]::new)).orElseGet(() -> new OptionalInt[0]);

        if (compute.length > 0)
        {
            if (vertex.isPresent() || fragment.isPresent() || geometry.isPresent())
                throw new IllegalStateException("Compute shaders must only have compute steps");
            if (Arrays.stream(compute).anyMatch(optional -> !optional.isPresent()))
                throw new IllegalStateException("All compute shaders must be valid");
        }
        else
        {
            if (!vertex.isPresent() || !fragment.isPresent())
                throw new IllegalStateException("Both vertex and fragment shaders must be defined for a standard shader program");
        }

        if (programId > 0)
            glDeleteProgram(programId);

        programId = glCreateProgram();
        if (compute.length <= 0)
        {
            glAttachShader(programId, vertex.getAsInt());
            glAttachShader(programId, fragment.getAsInt());
            if (geometry.isPresent())
                glAttachShader(programId, geometry.getAsInt());
        }
        else
        {
            for (OptionalInt shader : compute)
                glAttachShader(programId, shader.orElseThrow(() -> new IllegalStateException("All compute shaders must be valid")));
        }
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE)
            throw new ShaderException(glGetProgramInfoLog(programId, 512));
        return programId;
    }

    private static class Reloader implements PreparableReloadListener
    {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
        {
            if (!SelectiveReloadStateHandler.INSTANCE.get().test(VanillaResourceType.SHADERS))
                return stage.wait(null);
            return CompletableFuture.supplyAsync(() ->
            {
                Map<ShaderProgram.Shader, Map<ResourceLocation, CharSequence>> sources = new HashMap<>();
                for (ResourceLocation location : resourceManager.listResources("shaders/program", path -> ShaderProgram.Shader.byExtension(path) != null))
                {
                    ShaderProgram.Shader type = Objects.requireNonNull(ShaderProgram.Shader.byExtension(location.getPath()));
                    ResourceLocation id = new ResourceLocation(location.getNamespace(), location.getPath().substring(13, location.getPath().length() - type.getExtension().length()));
                    try (Resource resource = resourceManager.getResource(location))
                    {
                        sources.computeIfAbsent(type, key -> new HashMap<>()).put(id, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8));
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Failed to load shader: " + id, e);
                    }
                }
                return sources;
            }, backgroundExecutor).thenCompose(stage::wait).thenAcceptBothAsync(CompletableFuture.supplyAsync(() ->
            {
                Map<ResourceLocation, ShaderProgram> sources = new HashMap<>();
                for (ResourceLocation location : resourceManager.listResources("shaders/program_type", path -> path.endsWith(".json")))
                {
                    ResourceLocation id = new ResourceLocation(location.getNamespace(), location.getPath().substring(21, location.getPath().length() - 5));
                    try (Resource resource = resourceManager.getResource(location))
                    {
                        sources.put(id, ShaderProgram.CODEC.parse(JsonOps.INSTANCE, new JsonParser().parse(new InputStreamReader(resource.getInputStream()))).getOrThrow(false, LOGGER::error));
                    }
                    catch (Exception e)
                    {
                        LOGGER.error("Failed to load shader program: " + id, e);
                    }
                }
                return sources;
            }, backgroundExecutor).thenCompose(stage::wait), (sources, programs) ->
            {
                System.out.println(GL.getCapabilities());
                System.out.println("After Capabilities");
                SHADERS.values().stream().flatMap(map -> map.values().stream()).forEach(GL20C::glDeleteShader);
                SHADERS.clear();
                PROGRAMS.clear();
                PROGRAMS.putAll(programs);

                // Load all shaders
                for (ShaderProgram.Shader type : sources.keySet())
                {
                    for (Map.Entry<ResourceLocation, CharSequence> entry : sources.get(type).entrySet())
                    {
                        if (!type.isSupported())
                        {
                            LOGGER.warn(type + "");
                        }
                        try
                        {
                            SHADERS.computeIfAbsent(type, key -> new Object2IntArrayMap<>()).put(entry.getKey(), loadShader(entry.getValue(), type));
                        }
                        catch (Exception e)
                        {
                            LOGGER.error("Failed to load " + type.getDisplayName() + " Shader: " + entry.getKey(), e);
                        }
                    }
                }

                INSTANCES.keySet().removeIf(instance -> instance.getProgram() == 0); // Remove freed shaders

                // Re-link all created instances with the new shader ids
                INSTANCES.forEach((shaderInstance, program) ->
                {
                    try
                    {
                        shaderInstance.setProgram(linkShaders(program, shaderInstance.getProgram()));
                    }
                    catch (Exception e)
                    {
                        shaderInstance.free();
                        shaderInstance.setProgram(-1); // -1 indicates the program should still be refreshed, but that it is not valid
                        LOGGER.error("Failed to reload shader program: " + program, e);
                    }
                });

                LOGGER.info("Loaded " + sources.size() + " shaders and " + programs.size() + " shader programs.");
            }, task -> RenderSystem.recordRenderCall(task::run));
        }
    }
}
