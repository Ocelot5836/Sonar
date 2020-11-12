package io.github.ocelot.sonar.client.util;

import com.google.gson.Gson;
import io.github.ocelot.sonar.Sonar;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.*;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Manages adding a new resource pack with Sonar resources when in a development environment.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public final class SonarDevelopmentPack extends ResourcePack
{
    private static final Gson GSON = new Gson();
    private String[] resources;

    private SonarDevelopmentPack()
    {
        super(new File("Sonar Resources"));
    }

    /**
     * Internal. Do not call.
     */
    public static void init()
    {
        if (FMLLoader.isProduction())
            return;
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder()
        {
            @Override
            public <T extends ResourcePackInfo> void func_230230_a_(Consumer<T> packs, ResourcePackInfo.IFactory<T> packInfoFactory)
            {
                T t1 = ResourcePackInfo.createResourcePack(Sonar.DOMAIN + "_dev", true, SonarDevelopmentPack::new, packInfoFactory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.BUILTIN);
                if (t1 != null)
                {
                    packs.accept(t1);
                }
            }
        });
    }

    private String[] getResources() throws IOException
    {
        if (this.resources == null)
        {
            this.resources = JSONUtils.fromJson(GSON, IOUtils.toString(SonarDevelopmentPack.class.getResourceAsStream("/" + Sonar.DOMAIN + "_resources.json"), StandardCharsets.UTF_8), String[].class);
            if (this.resources == null)
                this.resources = new String[0];
        }

        return this.resources;
    }

    @Override
    protected InputStream getInputStream(String resourcePath)
    {
        return SonarDevelopmentPack.class.getResourceAsStream("/" + resourcePath);
    }

    @Override
    public boolean resourceExists(String resourcePath)
    {
        try
        {
            return Arrays.asList(this.getResources()).contains(resourcePath);
        }
        catch (IOException ignored)
        {
            return false;
        }
    }

    @Override
    public Set<String> getResourceNamespaces(ResourcePackType type)
    {
        return Collections.singleton(Sonar.DOMAIN);
    }

    @Override
    public void close()
    {
    }

    @Override
    public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
    {
        try
        {
            return Arrays.stream(this.getResources()).filter(resource -> namespaceIn.equals(Sonar.DOMAIN) && resource.startsWith(pathIn) && filterIn.test(resource)).map(resource -> new ResourceLocation(Sonar.DOMAIN, resource.substring(8 + Sonar.DOMAIN.length()))).collect(Collectors.toSet());
        }
        catch (IOException e)
        {
            return Collections.emptySet();
        }
    }

    @Nullable
    @Override
    public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException
    {
        T object;
        try (InputStream inputstream = this.getInputStream(Sonar.DOMAIN + "_pack.mcmeta"))
        {
            object = getResourceMetadata(deserializer, inputstream);
        }

        return object;
    }
}
