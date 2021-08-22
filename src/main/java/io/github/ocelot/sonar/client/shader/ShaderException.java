package io.github.ocelot.sonar.client.shader;

/**
 * <p>Exception thrown when shaders fail to compile.</p>
 *
 * @author Ocelot
 */
public class ShaderException extends Exception
{
    public ShaderException(String message)
    {
        super(message, null, true, true);
    }
}
