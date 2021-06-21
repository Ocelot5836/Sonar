package io.github.ocelot.sonar;

import me.shedaniel.architectury.annotations.ExpectPlatform;

import java.util.Arrays;
import java.util.concurrent.Executor;

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
    private static String parentModId;
    private static SonarModule[] modules;

    private Sonar()
    {
    }

    @ExpectPlatform
    private static String getActiveNamespace()
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void initClient(Runnable clientInit)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void initCommon(Runnable commonInit)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void setupClient(Runnable clientSetup)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void setupCommon(Runnable commonSetup)
    {
        throw new AssertionError();
    }

    /**
     * Initializes all core Sonar functionality and the requested modules. This should be called from the constructor of a mod.
     *
     * @param modules The modules to load. Duplicate modules are ignored
     */
    public static void init(SonarModule... modules)
    {
        Sonar.parentModId = getActiveNamespace();
        Sonar.modules = Arrays.stream(modules).distinct().toArray(SonarModule[]::new);
        initCommon(() -> Arrays.stream(Sonar.modules).filter(SonarModule::isCommonOnly).forEach(SonarModule::init));
        initClient(() -> Arrays.stream(Sonar.modules).filter(SonarModule::isClientOnly).forEach(SonarModule::init));
        setupCommon(() -> Arrays.stream(modules).filter(SonarModule::isCommonOnly).forEach(SonarModule::setup));
        setupClient(() -> Arrays.stream(modules).filter(SonarModule::isClientOnly).forEach(SonarModule::setup));
    }

    @ExpectPlatform
    public static Executor getSidedExecutor(boolean client)
    {
        throw new AssertionError();
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
     * @return The id of the mod hosting Sonar
     */
    public static String getParentModId()
    {
        return parentModId;
    }

    /**
     * @return An array of all modules loaded
     */
    public static SonarModule[] getLoadedModules()
    {
        return modules;
    }
}
