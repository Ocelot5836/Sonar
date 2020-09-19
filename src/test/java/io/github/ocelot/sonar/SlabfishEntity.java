package io.github.ocelot.sonar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Map;

public class SlabfishEntity extends Entity
{
    public SlabfishEntity(EntityType<?> p_i48580_1_, World p_i48580_2_)
    {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Override
    protected void registerData()
    {

    }

    @Override
    protected void readAdditional(CompoundNBT compound)
    {

    }

    @Override
    protected void writeAdditional(CompoundNBT compound)
    {

    }

    @Override
    public IPacket<?> createSpawnPacket()
    {
        return null;
    }

    public boolean hasBackpack()
    {
        return false;
    }

    public Map<Item, DyeColor> getSweaterMap()
    {
        return Collections.emptyMap();
    }
}
