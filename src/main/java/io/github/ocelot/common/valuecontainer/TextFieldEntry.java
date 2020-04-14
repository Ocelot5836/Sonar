package io.github.ocelot.common.valuecontainer;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * <p>Specifies that a {@link ValueContainerEntry} should use a custom text area validator if used.</p>
 *
 * @author Ocelot
 */
public interface TextFieldEntry
{
    /**
     * @return The validator or null for no validator to be used
     */
    Optional<Predicate<String>> getValidator();
}
