package io.github.ocelot.testmod;

import io.github.ocelot.testmod.block.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TestMod.MOD_ID)
public class TestMod
{
    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final ItemGroup TEST_GROUP = new ItemGroup(MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(TEST_BLOCK.get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCKS.register("test", () -> new TestBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> TEST_BLOCK_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().group(TEST_GROUP)));

    public TestMod()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::init);
        modBus.addListener(this::initClient);
    }

    private void init(FMLCommonSetupEvent event)
    {
    }

    private void initClient(FMLClientSetupEvent event)
    {
    }
}
