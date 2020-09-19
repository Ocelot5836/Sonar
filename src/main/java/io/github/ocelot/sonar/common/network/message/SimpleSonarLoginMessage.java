package io.github.ocelot.sonar.common.network.message;

/**
 * <p>An implementation of {@link SonarLoginMessage} that handles login specific data.</p>
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 4.0.0
 */
public abstract class SimpleSonarLoginMessage<T> implements SonarLoginMessage<T>
{
    private int loginIndex;

    @Override
    public int getAsInt()
    {
        return loginIndex;
    }

    @Override
    public void setLoginIndex(int index)
    {
        this.loginIndex = index;
    }
}
