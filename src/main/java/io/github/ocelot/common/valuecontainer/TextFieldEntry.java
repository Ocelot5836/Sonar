package io.github.ocelot.common.valuecontainer;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Specifies that a {@link ValueContainerEntry} should use a custom text area validator if used.</p>
 *
 * @author Ocelot
 * @since 2.1.0
 * @deprecated TODO remove in 4.0.0
 */
public interface TextFieldEntry
{
    /**
     * @return The validator or null for no validator to be used
     */
    Optional<Predicate<String>> getValidator();
}
