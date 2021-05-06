package io.github.ocelot.sonar.client;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.util.OnlineImageCache;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT)
public class RenderEvents
{
    @SubscribeEvent
    public static void onEvent(InputEvent.KeyInputEvent event)
    {
        if (event.getKey() == GLFW_KEY_L)
        {
            Minecraft.getInstance().displayGuiScreen(new TestScreen());
        }
    }
}
