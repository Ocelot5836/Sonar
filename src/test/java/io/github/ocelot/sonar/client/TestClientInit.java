package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.render.TestTileEntityRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TestClientInit
{
    public static final KeyMapping DUMP_RESOURCEPACKS = new KeyMapping("key." + TestMod.MOD_ID + ".dump_resourcepacks", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.misc");

    public static void init()
    {
        ClientRegistry.registerKeyBinding(DUMP_RESOURCEPACKS);
    }

    @SubscribeEvent
    public static void onEvent(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), ctx -> new TestTileEntityRenderer());
    }
}
