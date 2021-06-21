package io.github.ocelot.sonar.core;

import me.shedaniel.architectury.annotations.ExpectPlatform;

/**
 * <p>Manages adding a new resource pack with Sonar resources when in a development environment.</p>
 *
 * @author Ocelot
 * @since 5.1.0
 */
public final class SonarDevelopmentPack
{
    private SonarDevelopmentPack()
    {
    }

    @ExpectPlatform
    public static void init()
    {
    }
}
