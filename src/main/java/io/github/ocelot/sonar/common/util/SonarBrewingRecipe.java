package io.github.ocelot.sonar.common.util;

import com.google.common.base.Suppliers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.function.Supplier;

/**
 * <p>This class exists purely because the {@link net.minecraftforge.common.brewing.BrewingRecipe} doesn't work good enough. This supports brewing a potion type to another potion type.</p>
 *
 * @author Ocelot
 * @since 6.2.0
 */
public class SonarBrewingRecipe implements IBrewingRecipe
{
    private final Supplier<Potion> input;
    private final Supplier<Ingredient> ingredient;
    private final Supplier<Potion> result;

    public SonarBrewingRecipe(Supplier<Potion> input, Supplier<Ingredient> ingredient, Supplier<Potion> result)
    {
        this.input = input;
        this.ingredient = Suppliers.memoize(ingredient::get);
        this.result = result;
    }

    @Override
    public boolean isInput(ItemStack stack)
    {
        Item item = stack.getItem();
        return (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION) && PotionUtils.getPotion(stack) == this.input.get();
    }

    @Override
    public boolean isIngredient(ItemStack stack)
    {
        return this.ingredient.get().test(stack);
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient)
    {
        if (!input.isEmpty() && !ingredient.isEmpty() && this.isInput(input) && this.isIngredient(ingredient))
            return PotionUtils.setPotion(new ItemStack(input.getItem()), this.result.get());
        return ItemStack.EMPTY;
    }
}
