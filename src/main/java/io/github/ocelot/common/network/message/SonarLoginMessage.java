package io.github.ocelot.common.network.message;

import java.util.function.IntSupplier;

/**
 * <p>An implementation of {@link SonarMessage} intended for login messages.</p>
 *
 * @param <T> The interface that should handle this message
 * @author Ocelot
 * @since 3.2.0
 */
public interface SonarLoginMessage<T> extends SonarMessage<T>, IntSupplier
{
    /**
     * @return The index of this login message.
     */
    @Override
    int getAsInt();

    /**
     * Sets the index for the login message. Should not usually be called.
     *
     * @param index The new login index
     */
    void setLoginIndex(int index);
}
