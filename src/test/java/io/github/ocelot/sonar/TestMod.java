package io.github.ocelot.sonar;

import io.github.ocelot.sonar.block.TestBlock;
import io.github.ocelot.sonar.block.TestStateBlock;
import io.github.ocelot.sonar.client.TestClientInit;
import io.github.ocelot.sonar.client.tooltip.TooltipColorManager;
import io.github.ocelot.sonar.common.item.FishBucketItemBase;
import io.github.ocelot.sonar.common.item.SpawnEggItemBase;
import io.github.ocelot.sonar.common.item.ValueContainerEditorItem;
import io.github.ocelot.sonar.common.valuecontainer.OpenValueContainerMessage;
import io.github.ocelot.sonar.entity.TestEntity;
import io.github.ocelot.sonar.network.TestMessageHandler;
import io.github.ocelot.sonar.tileentity.TestTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(TestMod.MOD_ID)
public class TestMod
{
    public static final String MOD_ID = "examplemod";

    public static final ItemGroup TEST_GROUP = new ItemGroup(MOD_ID)
    {
        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(TEST_BLOCK.get());
        }
    };

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCKS.register("test", () -> new TestBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<TestStateBlock> TEST_STATE_BLOCK = BLOCKS.register("test_state", () -> new TestStateBlock(Block.Properties.from(Blocks.IRON_BLOCK)));
    public static final RegistryObject<BlockItem> TEST_BLOCK_ITEM = ITEMS.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<BlockItem> TEST_STATE_BLOCK_ITEM = ITEMS.register("test_state", () -> new BlockItem(TEST_STATE_BLOCK.get(), new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<Item> TEST_EDITOR_ITEM = ITEMS.register("test_value_container_editor", () -> new ValueContainerEditorItem(new Item.Properties().group(TEST_GROUP), (player, pos) -> TestMessageHandler.PLAY.send(PacketDistributor.PLAYER.with(() -> player), new OpenValueContainerMessage(pos))));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY_A = ENTITIES.register("test_entity_a", () -> EntityType.Builder.create(TestEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).build("test_entity_a"));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY_B = ENTITIES.register("test_entity_b", () -> EntityType.Builder.create(TestEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).build("test_entity_a"));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY_C = ENTITIES.register("test_entity_c", () -> EntityType.Builder.create(TestEntity::new, EntityClassification.CREATURE).size(0.6F, 0.7F).build("test_entity_a"));
    public static final RegistryObject<Item> TEST_ENTITY_A_SPAWN_EGG = ITEMS.register("test_entity_a_spawn_egg", () -> new SpawnEggItemBase<>(TEST_ENTITY_A, 0xff00ff, 0x7f007f, true, new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<Item> TEST_ENTITY_B_SPAWN_EGG = ITEMS.register("test_entity_b_spawn_egg", () -> new SpawnEggItemBase<>(TEST_ENTITY_B, 0x00ffff, 0x007f7f, true, new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<Item> TEST_ENTITY_C_SPAWN_EGG = ITEMS.register("test_entity_c_spawn_egg", () -> new SpawnEggItemBase<>(TEST_ENTITY_C, 0x00ff00, 0x007f00, true, new Item.Properties().group(TEST_GROUP)));
    public static final RegistryObject<FishBucketItemBase> TEST_ENTITY_C_BUCKET = ITEMS.register("test_entity_c_bucket", () -> new FishBucketItemBase(TEST_ENTITY_C, () -> Fluids.WATER, true, new Item.Properties().group(TEST_GROUP)));

    public static final RegistryObject<TileEntityType<TestTileEntity>> TEST_TILE_ENTITY = TILE_ENTITIES.register("test", () -> TileEntityType.Builder.create(TestTileEntity::new, TEST_BLOCK.get()).build(null));

    public TestMod()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        TooltipColorManager.register(modBus);
        BLOCKS.register(modBus);
        TILE_ENTITIES.register(modBus);
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
        modBus.addListener(this::init);
        modBus.addListener(this::initClient);
    }

    private void init(FMLCommonSetupEvent event)
    {
        TestMessageHandler.init();
    }

    private void initClient(FMLClientSetupEvent event)
    {
        TestClientInit.init();
    }
}
