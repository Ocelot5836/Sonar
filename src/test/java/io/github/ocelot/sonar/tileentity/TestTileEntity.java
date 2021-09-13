package io.github.ocelot.sonar.tileentity;

import io.github.ocelot.sonar.TestMod;
import io.github.ocelot.sonar.common.tileentity.BaseTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TestTileEntity extends BaseTileEntity
{
    public TestTileEntity(BlockPos pos, BlockState state)
    {
        super(TestMod.TEST_TILE_ENTITY.get(), pos, state);
    }
}
