package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.render.TestTileEntityRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.glfw.GLFW;

public class TestClientInit
{
    public static final KeyMapping DUMP_RESOURCEPACKS = new KeyMapping("key." + TestMod.MOD_ID + ".dump_resourcepacks", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.misc");

    public static void init()
    {
        ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);

        ClientRegistry.registerKeyBinding(DUMP_RESOURCEPACKS);
    }
}
