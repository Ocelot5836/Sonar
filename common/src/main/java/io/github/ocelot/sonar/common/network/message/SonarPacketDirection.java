package io.github.ocelot.sonar.common.network.message;

/**
 * <p>Used by the Sonar networking API to determine what direction packets are going. Based on forge's NetworkDirection.</p>
 *
 * @author Ocelot
 * @since 7.0.0
 */
public enum SonarPacketDirection
{
    PLAY_SERVERBOUND, PLAY_CLIENTBOUND, LOGIN_SERVERBOUND, LOGIN_CLIENTBOUND;

    public boolean isClientbound()
    {
        return this == PLAY_CLIENTBOUND || this == LOGIN_CLIENTBOUND;
    }

    public boolean isServerbound()
    {
        return this == PLAY_SERVERBOUND || this == LOGIN_SERVERBOUND;
    }
}
