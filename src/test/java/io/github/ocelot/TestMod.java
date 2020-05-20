package io.github.ocelot;

import io.github.ocelot.block.TestBlock;
import io.github.ocelot.client.render.TestTileEntityRenderer;
import io.github.ocelot.item.TestValueContainerEditorItem;
import io.github.ocelot.network.TestMessageHandler;
import io.github.ocelot.tileentity.TestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCKS.register("test", () -> new TestBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> TEST_BLOCK_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<Item> TEST_EDITOR_ITEM = ITEMS.register("test_value_container_editor", () -> new TestValueContainerEditorItem(new Item.Properties().group(TEST_GROUP)));

    public static final RegistryObject<TileEntityType<TestTileEntity>> TEST_TILE_ENTITY = TILE_ENTITIES.register("test", () -> TileEntityType.Builder.create(TestTileEntity::new, TEST_BLOCK.get()).build(null));

    public TestMod()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        TILE_ENTITIES.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(this::init);
        modBus.addListener(this::initClient);
    }

    private void init(FMLCommonSetupEvent event)
    {
        TestMessageHandler.init();
    }

    private void initClient(FMLClientSetupEvent event)
    {
        ClientRegistry.bindTileEntityRenderer(TEST_TILE_ENTITY.get(), TestTileEntityRenderer::new);
    }
}
