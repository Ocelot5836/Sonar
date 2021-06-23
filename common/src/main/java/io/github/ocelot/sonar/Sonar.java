package io.github.ocelot.sonar;

import me.shedaniel.architectury.annotations.ExpectPlatform;

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
    private static SonarModContext context;
    private static SonarModule[] modules;

    private Sonar()
    {
    }

    @ExpectPlatform
    private static void initClient(SonarModContext context, Runnable clientInit)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void initCommon(SonarModContext context, Runnable commonInit)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void setupClient(SonarModContext context, Runnable clientSetup)
    {
        throw new AssertionError();
    }

    @ExpectPlatform
    private static void setupCommon(SonarModContext context, Runnable commonSetup)
    {
        throw new AssertionError();
    }

    /**
     * Initializes all core Sonar functionality and the requested modules. This should be called from the constructor of a mod.
     *
     * @param modules The modules to load. Duplicate modules are ignored
     */
    public static void init(SonarModContext context, SonarModule... modules)
    {
        Sonar.context = context;
        Sonar.modules = Arrays.stream(modules).distinct().toArray(SonarModule[]::new);
        initCommon(context, () -> Arrays.stream(Sonar.modules).filter(SonarModule::isCommonOnly).forEach(SonarModule::init));
        initClient(context, () -> Arrays.stream(Sonar.modules).filter(SonarModule::isClientOnly).forEach(SonarModule::init));
        setupCommon(context, () -> Arrays.stream(modules).filter(SonarModule::isCommonOnly).forEach(SonarModule::setup));
        setupClient(context, () -> Arrays.stream(modules).filter(SonarModule::isClientOnly).forEach(SonarModule::setup));
    }

    /**
     * @return The context of the mod hosting Sonar
     */
    public static SonarModContext context()
    {
        return context;
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
