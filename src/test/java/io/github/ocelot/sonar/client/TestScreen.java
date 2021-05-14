package io.github.ocelot.sonar.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.ocelot.sonar.client.framebuffer.AdvancedFbo;
import io.github.ocelot.sonar.client.render.ShapeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

public class TestScreen extends Screen
{
    private final AdvancedFbo fbo = new AdvancedFbo.Builder(4096, 4096).addColorTextureBuffer().setDepthRenderBuffer().build();

    public TestScreen()
    {
        super(StringTextComponent.EMPTY);
        this.fbo.create();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.fbo.bind(true);
        this.fbo.clear();
        AbstractGui.fill(matrixStack, 0, 0, 512, 512, 0xFFFF00FF);
        Minecraft.getInstance().getFramebuffer().bindFramebuffer(true);

        this.fbo.getColorTextureAttachment(0).bindAttachment();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        ShapeRenderer.drawRectWithTexture(matrixStack, 0, 0, 0, 0, this.width, this.height, 1, 1, 1, 1);
    }

    @Override
    public void onClose()
    {
        this.fbo.free();
    }
}