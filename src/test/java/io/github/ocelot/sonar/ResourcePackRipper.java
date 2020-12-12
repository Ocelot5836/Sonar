package io.github.ocelot.sonar;

import com.google.common.base.Stopwatch;
import io.github.ocelot.sonar.client.TestClientInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.FilePack;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.SimpleResource;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

/**
 * <p>Saves all enabled ZIP resource packs to a local file cache.</p>
 *
 * @author Ocelot
 */
@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT)
public class ResourcePackRipper
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean running;

    private static void log(String message, Object... args)
    {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((new StringTextComponent("")).append((new TranslationTextComponent("debug.prefix")).mergeStyle(TextFormatting.YELLOW, TextFormatting.BOLD)).appendString(" ").append(new TranslationTextComponent(TestMod.MOD_ID + ".resource_pack_dump." + message, args)));
    }

    @SubscribeEvent
    public static void onEvent(InputEvent event)
    {
        if (TestClientInit.DUMP_RESOURCEPACKS.isPressed())
        {
            if (running)
            {
                log("already_saving");
                return;
            }

            Set<ClientResourcePackInfo> resourcePacks = Minecraft.getInstance().getResourcePackList().getEnabledPacks().stream().filter(info -> info.getResourcePack() instanceof FilePack).collect(Collectors.toSet());
            if (resourcePacks.isEmpty())
            {
                log("none");
                return;
            }

            log("saving", resourcePacks.size());

            running = true;
            Path output = Paths.get(Minecraft.getInstance().gameDir.toURI()).resolve("resourcepack-dump");
            CompletableFuture.supplyAsync(() ->
            {
                try
                {
                    FileUtils.deleteDirectory(output.toFile());
                    Files.createDirectories(output);
                    return true;
                }
                catch (Exception e)
                {
                    LOGGER.error("Failed to create '" + output + "'", e);
                    return false;
                }
            }, Util.getRenderingService()).thenAcceptAsync(success ->
            {
                if (!success)
                {
                    running = false;
                    return;
                }

                Stopwatch stopwatch = Stopwatch.createStarted();
                CompletableFuture.allOf(resourcePacks.stream().filter(info -> info.getResourcePack() instanceof FilePack).map(info ->
                {
                    String name = info.getName();
                    IResourcePack resourcePack = info.getResourcePack();
                    Path packFolder = output.resolve(name);

                    return CompletableFuture.runAsync(() ->
                    {
                        ZipFile zipFile;
                        try
                        {
                            zipFile = (ZipFile) ObfuscationReflectionHelper.findMethod(FilePack.class, "func_195773_b").invoke(resourcePack);
                            if (zipFile == null)
                                throw new IOException("Failed to retrieve zip from resource pack '" + name + "'");
                        }
                        catch (Exception e)
                        {
                            LOGGER.error("Failed to dump resource pack '" + name + "' to '" + packFolder + "'", e);
                            return;
                        }

                        Collections.list(zipFile.entries()).forEach(entry ->
                        {
                            Path file = packFolder.resolve(entry.getName());

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
                                LOGGER.error("Failed to write ZIP entry '" + entry.getName() + "' to '" + file + "'", e);
                            }
                        });
                    }, Util.getRenderingService());
                }).toArray(CompletableFuture[]::new)).
                        thenRunAsync(() ->
                        {
                            log("complete", stopwatch);
                            running = false;
                            Util.getOSType().openFile(output.toFile());
                        }, Minecraft.getInstance());
            }, Minecraft.getInstance());
        }
    }
}
