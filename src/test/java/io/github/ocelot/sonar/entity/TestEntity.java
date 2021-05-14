package io.github.ocelot.sonar.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TestEntity extends BeeEntity
{
    public TestEntity(EntityType<? extends BeeEntity> type, World world)
    {
        super(type, world);
    }

    @Override
    public IPacket<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
