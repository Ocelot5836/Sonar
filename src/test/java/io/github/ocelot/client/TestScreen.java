package io.github.ocelot.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class TestScreen extends Screen implements TooltipRenderer
{
    protected TestScreen(ITextComponent p_i51108_1_)
    {
        super(p_i51108_1_);
    }

    @Override
    public void renderTooltip(ItemStack stack, int posX, int posY)
    {
        super.renderTooltip(stack, posX, posY);
    }

    @Override
    public void renderComponentHoverEffect(@Nullable ITextComponent textComponent, int posX, int posY)
    {
        super.renderComponentHoverEffect(textComponent, posX, posY);
    }
}
