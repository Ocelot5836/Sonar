package io.github.ocelot.sonar.forge;

import io.github.ocelot.sonar.SonarModContext;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SonarModContextImpl implements SonarModContext
{
    private final ModContainer container;
    private final IEventBus modBus;

    private SonarModContextImpl(ModContainer container, ModLoadingContext context)
    {
        this.container = container;
        this.modBus = context.<FMLJavaModLoadingContext>extension().getModEventBus();
    }

    public static SonarModContext get(Object... parameters)
    {
        Validate.isTrue(parameters.length == 1 && parameters[0].getClass() == ModLoadingContext.class, "Forge requires the mod context as the parameter");
        ModLoadingContext context = (ModLoadingContext) parameters[0];
        String modId = context.getActiveNamespace();
        return new SonarModContextImpl(ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("Unknown mod with id: " + modId)), context);
    }

    public IEventBus getModBus()
    {
        return modBus;
    }

    @Override
    public String getParentModId()
    {
        return this.container.getModId();
    }

    @Override
    public BlockableEventLoop<?> getSidedExecutor(boolean client)
    {
        return LogicalSidedProvider.WORKQUEUE.get(client ? LogicalSide.CLIENT : LogicalSide.SERVER);
    }
}
