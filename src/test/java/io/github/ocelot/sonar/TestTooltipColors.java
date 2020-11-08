package io.github.ocelot.sonar;

import io.github.ocelot.sonar.client.tooltip.TooltipColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.Locale;

@Mod.EventBusSubscriber(modid = TestMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum TestTooltipColors implements TooltipColor
{
    RAINBOW;

    @Override
    public int getBorderStartColor()
    {
        long time = Minecraft.getInstance().world == null ? 0 : Minecraft.getInstance().world.getGameTime();
        return 0xff000000 | MathHelper.hsvToRGB((time % 50) / 50f, 1.0F, 1.0F);
    }

    @Override
    public int getBorderEndColor()
    {
        return this.getBorderStartColor();
    }

    @Override
    public int getBackgroundColor()
    {
        return 0;
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<TooltipColor.RegistryWrapper> event)
    {
        event.getRegistry().registerAll(Arrays.stream(values()).map(tooltipColor -> tooltipColor.wrap().setRegistryName(new ResourceLocation(TestMod.MOD_ID, tooltipColor.name().toLowerCase(Locale.ROOT)))).toArray(TooltipColor.RegistryWrapper[]::new));
    }
}
