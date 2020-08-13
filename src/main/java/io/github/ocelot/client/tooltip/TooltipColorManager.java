package io.github.ocelot.client.tooltip;

import io.github.ocelot.Sonar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>Manages tooltips for items that have custom tooltip colors.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
@SuppressWarnings("unused")
public class TooltipColorManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, ResourceLocation> RESOURCE_CACHE = new HashMap<>();
    private static final Map<String, Integer> COLOR_CACHE = new HashMap<>();
    private static final Set<String> ERRORED_COLOR_CACHE = new HashSet<>();

    public static IForgeRegistry<TooltipColor.RegistryWrapper> TOOLTIP_COLORS;

    /**
     * Registers the required events for tooltip colors to function.
     *
     * @param bus The mod event bus. This should come from {@link FMLJavaModLoadingContext#getModEventBus()}
     */
    public static void register(IEventBus bus)
    {
        bus.addListener(TooltipColorManager::registerRegistries);
        MinecraftForge.EVENT_BUS.register(TooltipColorManager.class);
    }

    private static void registerRegistries(RegistryEvent.NewRegistry event)
    {
        TOOLTIP_COLORS = new RegistryBuilder<TooltipColor.RegistryWrapper>().setName(new ResourceLocation(Sonar.DOMAIN, "tooltip_colors")).setType(TooltipColor.RegistryWrapper.class).create();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onEvent(RenderTooltipEvent.Color event)
    {
        ItemStack stack = event.getStack();
        int borderStartColor = getBorderStartColor(stack);
        int borderEndColor = getBorderEndColor(stack);
        int backgroundColor = getBackgroundColor(stack);
        if (borderStartColor != 0)
            event.setBorderStart(borderStartColor);
        if (borderEndColor != 0)
            event.setBorderEnd(borderEndColor);
        if (backgroundColor != 0)
            event.setBackground(backgroundColor);
    }

    @Nullable
    private static TooltipColor getTooltipColor(CompoundNBT nbt)
    {
        return nbt.contains("Color", Constants.NBT.TAG_STRING) ? TOOLTIP_COLORS.getValue(RESOURCE_CACHE.computeIfAbsent(nbt.getString("Color"), ResourceLocation::new)) : null;
    }

    private static int getColor(CompoundNBT nbt, String name, int defaultColor)
    {
        if (nbt.contains(name, Constants.NBT.TAG_STRING))
        {
            String hex = nbt.getString(name);
            if (ERRORED_COLOR_CACHE.contains(hex))
                return defaultColor;
            if (COLOR_CACHE.containsKey(hex))
                return COLOR_CACHE.get(hex);

            try
            {
                int color = Integer.parseUnsignedInt(nbt.getString(name), 16);
                COLOR_CACHE.put(hex, color);
                return color;
            }
            catch (Exception e)
            {
                LOGGER.warn("Could not parse tooltip color '" + name + "' as hex", e);
                ERRORED_COLOR_CACHE.add(hex);
                return 0;
            }
        }
        else if (nbt.contains(name, Constants.NBT.TAG_ANY_NUMERIC))
        {
            return nbt.getInt(name);
        }
        return defaultColor;
    }

    /**
     * Checks the stack for a tag defining the border start color.
     *
     * @param stack The stack to get the color from
     * @return The color of the tooltip border start or zero for no change
     */
    public static int getBorderStartColor(ItemStack stack)
    {
        CompoundNBT nbt = stack.getChildTag("Tooltip");
        if (nbt == null)
            return 0;
        TooltipColor tooltipColor = getTooltipColor(nbt);
        int borderColor = getColor(nbt, "BorderColor", 0);
        return getColor(nbt, "BorderStartColor", borderColor != 0 ? borderColor : tooltipColor != null ? tooltipColor.getBorderStartColor() : 0);
    }

    /**
     * Checks the stack for a tag defining the border end color.
     *
     * @param stack The stack to get the color from
     * @return The color of the tooltip border end or zero for no change
     */
    public static int getBorderEndColor(ItemStack stack)
    {
        CompoundNBT nbt = stack.getChildTag("Tooltip");
        if (nbt == null)
            return 0;
        TooltipColor tooltipColor = getTooltipColor(nbt);
        int borderColor = getColor(nbt, "BorderColor", 0);
        return getColor(nbt, "BorderEndColor", borderColor != 0 ? borderColor : tooltipColor != null ? tooltipColor.getBorderStartColor() : 0);
    }


    /**
     * Checks the stack for a tag defining the background color.
     *
     * @param stack The stack to get the color from
     * @return The color of the tooltip background or zero for no change
     */
    public static int getBackgroundColor(ItemStack stack)
    {
        CompoundNBT nbt = stack.getChildTag("Tooltip");
        if (nbt == null)
            return 0;
        TooltipColor tooltipColor = getTooltipColor(nbt);
        return getColor(nbt, "BackgroundColor", tooltipColor != null ? tooltipColor.getBackgroundColor() : 0);
    }
}
