package io.github.ocelot.sonar.core.forge;

import com.google.gson.Gson;
import io.github.ocelot.sonar.Sonar;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.util.GsonHelper;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p>Manages adding a new resource pack with Sonar resources when in a development environment.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public final class SonarDevelopmentPackImpl extends AbstractPackResources
{
    private static final Gson GSON = new Gson();
    private String[] resources;

    private SonarDevelopmentPackImpl()
    {
        super(new File("Sonar Resources"));
    }

    public static void init()
    {
        if (FMLLoader.isProduction() || Minecraft.getInstance() == null)
            return;
        Minecraft.getInstance().getResourcePackRepository().addPackFinder((packs, packInfoFactory) ->
        {
            Pack t1 = Pack.create(Sonar.DOMAIN + "_dev", true, SonarDevelopmentPackImpl::new, packInfoFactory, Pack.Position.TOP, PackSource.BUILT_IN);
            if (t1 != null)
            {
                packs.accept(t1);
            }
        });
    }

    private String[] getResources() throws IOException
    {
        if (this.resources == null)
        {
            try (InputStream stream = SonarDevelopmentPackImpl.class.getResourceAsStream("/" + Sonar.DOMAIN + "_resources.json"))
            {
                if (stream != null)
                    this.resources = GsonHelper.fromJson(GSON, IOUtils.toString(stream, StandardCharsets.UTF_8), String[].class);
                if (this.resources == null)
                    this.resources = new String[0];
            }
        }

        return this.resources;
    }

    @Override
    protected InputStream getResource(String resourcePath)
    {
        return SonarDevelopmentPackImpl.class.getResourceAsStream("/" + resourcePath);
    }

    @Override
    public boolean hasResource(String resourcePath)
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
    public Set<String> getNamespaces(PackType type)
    {
        return Collections.singleton(Sonar.DOMAIN);
    }

    @Override
    public void close()
    {
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType type, String namespaceIn, String pathIn, int maxDepthIn, Predicate<String> filterIn)
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
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException
    {
        T object;
        try (InputStream inputstream = this.getResource(Sonar.DOMAIN + "_pack.mcmeta"))
        {
            object = getMetadataFromStream(deserializer, inputstream);
        }

        return object;
    }

    @Override
    public boolean isHidden()
    {
        return true;
    }
}
