package io.github.ocelot.sonar;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Arrays;

/**
 * <p>Contains static information about Sonar.</p>
 *
 * @author Ocelot
 * @since 3.2.0
 */
public final class Sonar
{
    /**
     * The domain (modid) used for resource locations.
     */
    public static final String DOMAIN = "sonar";
    private static SonarModule[] modules;

    private Sonar()
    {
    }

    /**
     * Initializes all core Sonar functionality and the requested modules. This should be called from the constructor of a mod.
     *
     * @param modBus  The main bus for registering events
     * @param modules The modules to load. Duplicate modules are ignored
     */
    public static void init(IEventBus modBus, SonarModule... modules)
    {
        Sonar.modules = Arrays.stream(modules).distinct().toArray(SonarModule[]::new);
        Arrays.stream(Sonar.modules).filter(module -> !module.isClientOnly()).forEach(module -> module.init(modBus));
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Arrays.stream(Sonar.modules).filter(SonarModule::isClientOnly).forEach(module -> module.init(modBus)));
        modBus.addListener(Sonar::setup);
        modBus.addListener(Sonar::setupClient);
    }

    private static void setup(FMLCommonSetupEvent event)
    {
        Arrays.stream(modules).filter(module -> !module.isClientOnly()).forEach(SonarModule::setup);
    }

    private static void setupClient(FMLClientSetupEvent event)
    {
        Arrays.stream(modules).filter(SonarModule::isClientOnly).forEach(SonarModule::setup);
    }

    /**
     * Checks to see if the specified module has been loaded.
     *
     * @param module The module to load
     * @return Whether or not that module is loaded
     */
    public static boolean isModuleLoaded(SonarModule module)
    {
        return Arrays.stream(modules).anyMatch(m -> m == module);
    }

    /**
     * @return An array of all modules loaded
     */
    public static SonarModule[] getLoadedModules()
    {
        return modules;
    }
}
