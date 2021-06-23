package io.github.ocelot.sonar.core;

import com.google.gson.Gson;
import io.github.ocelot.sonar.Sonar;
import me.shedaniel.architectury.annotations.ExpectPlatform;
import me.shedaniel.architectury.annotations.PlatformOnly;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;

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

@ApiStatus.Internal
public final class SonarDevelopmentPack extends AbstractPackResources
{
    private static final Gson GSON = new Gson();
    private String[] resources;

    public SonarDevelopmentPack()
    {
        super(new File("Sonar Resources"));
    }

    @ExpectPlatform
    public static void init()
    {
    }

    private String[] getResources() throws IOException
    {
        if (this.resources == null)
        {
            try (InputStream stream = this.getResource("resources.json"))
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
        return SonarDevelopmentPack.class.getResourceAsStream("/resourcepacks/" + Sonar.DOMAIN + "/" + resourcePath);
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

    @PlatformOnly(PlatformOnly.FORGE)
    public boolean isHidden()
    {
        return true;
    }
}
