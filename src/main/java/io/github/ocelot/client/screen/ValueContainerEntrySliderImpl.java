package io.github.ocelot.client.screen;

import io.github.ocelot.common.valuecontainer.SliderEntry;
import io.github.ocelot.common.valuecontainer.ValueContainerEntry;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>A simple implementation of an {@link AbstractSlider} that can be used to modify {@link SliderEntry}.</p>
 *
 * @author Ocelot
 * @since 2.2.0
 */
@OnlyIn(Dist.CLIENT)
public class ValueContainerEntrySliderImpl extends AbstractSlider
{
    private final DecimalFormat format;
    private final ValueContainerEntry<?> entry;
    private final SliderEntry sliderEntry;

    public ValueContainerEntrySliderImpl(ValueContainerEntry<?> entry, int x, int y, int width, int height)
    {
        super(x, y, width, height, entry.getDisplayName(), 0.0);
        if (!(entry instanceof SliderEntry))
            throw new IllegalStateException("Entry '" + entry + "' needs to implement SliderEntry in order to use the SLIDER type");
        this.format = this.createDecimalFormat();
        this.entry = entry;
        this.sliderEntry = (SliderEntry) entry;
        this.field_230683_b_ = (this.sliderEntry.getSliderValue() - this.sliderEntry.getMinSliderValue()) / (this.sliderEntry.getMaxSliderValue() - this.sliderEntry.getMinSliderValue());
        this.func_230979_b_();
    }

    @Override
    protected void func_230979_b_()
    {
        if (this.sliderEntry.isPercentage())
        {
            this.setMessage(new StringTextComponent(this.format.format(Math.floor(this.field_230683_b_ * 100.0)) + "%"));
        }
        else
        {
            double sliderValue = this.field_230683_b_ * (this.sliderEntry.getMaxSliderValue() - this.sliderEntry.getMinSliderValue()) + this.sliderEntry.getMinSliderValue();
            this.setMessage(new StringTextComponent(this.format.format(this.sliderEntry.isDecimal() ? sliderValue : Math.floor(sliderValue))));
        }
    }

    @Override
    protected void func_230972_a_()
    {
        Optional<Predicate<String>> optional = this.entry.getValidator();
        double sliderValue = this.field_230683_b_ * (this.sliderEntry.getMaxSliderValue() - this.sliderEntry.getMinSliderValue()) + this.sliderEntry.getMinSliderValue();
        String value = String.valueOf(this.sliderEntry.isDecimal() ? sliderValue : Math.floor(sliderValue));
        if (optional.isPresent() && !optional.get().test(value))
            return;

        this.entry.parse(value);
    }

    /**
     * @return A new decimal format for decimal values on the slider
     */
    protected DecimalFormat createDecimalFormat()
    {
        DecimalFormat decimalformat = new DecimalFormat("#.#");
        decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        return decimalformat;
    }

    /**
     * @return The format of decimal values
     */
    public DecimalFormat getFormat()
    {
        return format;
    }

    /**
     * @return The entry this slider modifies
     */
    public ValueContainerEntry<?> getEntry()
    {
        return entry;
    }

    /**
     * @return The entry this slider modifies as a slider specific entry
     */
    public SliderEntry getSliderEntry()
    {
        return sliderEntry;
    }
}
