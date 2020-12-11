package io.github.ocelot.sonar;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.zip.ZipFile;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT)
public class ResourcePackRipper
{
    @SubscribeEvent
    public static void onEvent(InputEvent.KeyInputEvent event)
    {
        if (event.getKey() == GLFW.GLFW_KEY_J)
        {
            Minecraft.getInstance().getResourcePackList().getAllPacks().forEach(info ->
            {
                try
                {
                    IResourcePack resourcePack = info.getResourcePack();
                    if (!(resourcePack instanceof FilePack))
                        return;

                    ZipFile zipFile = (ZipFile) ObfuscationReflectionHelper.findMethod(FilePack.class, "func_195773_b").invoke(resourcePack);
                    if (zipFile == null)
                        throw new RuntimeException("Failed to retrieve zip from file pack");

                    Path output = Paths.get(Minecraft.getInstance().gameDir.toURI()).resolve("output").resolve(info.getName());
                    if (!Files.exists(output))
                        Files.createDirectories(output);

                    Collections.list(zipFile.entries()).forEach(entry ->
                    {
                        Path file = output.resolve(entry.getName());

                        try
                        {
                            if (entry.isDirectory())
                            {
                                if (!Files.exists(file))
                                    Files.createDirectories(file);
                            }
                            else
                            {
                                if (!Files.exists(file.getParent()))
                                    Files.createDirectories(file.getParent());
                                if (!Files.exists(file))
                                    Files.createFile(file);
                                try (FileOutputStream os = new FileOutputStream(file.toFile()))
                                {
                                    IOUtils.copy(zipFile.getInputStream(entry), os);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            });
        }
    }
}
