package io.github.ocelot.sonar.client;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.client.render.TestTileEntityRenderer;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.lwjgl.glfw.GLFW;

public class TestClientInit
{
    public static final KeyBinding DUMP_RESOURCEPACKS = new KeyBinding("key." + TestMod.MOD_ID + ".dump_resourcepacks", KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_J, "key.categories.misc");

    public static void init()
    {
        ClientRegistry.bindTileEntityRenderer(TestMod.TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);

        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_A.get(), BeeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_B.get(), BeeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TestMod.TEST_ENTITY_C.get(), BeeRenderer::new);

        ClientRegistry.registerKeyBinding(DUMP_RESOURCEPACKS);
    }
}
