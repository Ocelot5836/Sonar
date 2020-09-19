package io.github.ocelot.sonar.client;

import io.github.ocelot.sonar.client.tooltip.TooltipColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Locale;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public enum TestTooltipColor implements TooltipColor
{
    TEST(0xffff00ff, 0xffff00ff, 0xff7f007f);

    private final int borderStartColor;
    private final int borderEndColor;
    private final int backgroundColor;

    TestTooltipColor(int borderStartColor, int borderEndColor, int backgroundColor)
    {
        this.borderStartColor = borderStartColor;
        this.borderEndColor = borderEndColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int getBorderStartColor()
    {
        return borderStartColor;
    }

    @Override
    public int getBorderEndColor()
    {
        return borderEndColor;
    }

    @Override
    public int getBackgroundColor()
    {
        return backgroundColor;
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<TooltipColor.RegistryWrapper> event)
    {
        event.getRegistry().registerAll(Arrays.stream(TestTooltipColor.values()).map(color -> color.wrap().setRegistryName(color.name().toLowerCase(Locale.ROOT))).toArray(TooltipColor.RegistryWrapper[]::new));
    }
}
