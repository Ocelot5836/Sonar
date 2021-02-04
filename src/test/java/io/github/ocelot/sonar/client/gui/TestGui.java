package io.github.ocelot.sonar.client.gui;

import io.github.ocelot.sonar.client.util.OnlineImageCache;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;

import java.util.concurrent.TimeUnit;

public class TestGui extends Screen
{
    private static final OnlineImageCache CACHE = new OnlineImageCache(10, TimeUnit.MINUTES);

    public TestGui()
    {
        super(new StringTextComponent("Test"));
    }

    @Override
    protected void init()
    {
    }
}
