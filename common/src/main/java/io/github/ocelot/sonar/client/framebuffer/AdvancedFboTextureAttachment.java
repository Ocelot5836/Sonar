package io.github.ocelot.sonar.client.framebuffer;

/**
 * <p>A texture attachment added to an {@link AdvancedFbo}</p>
 *
 * @author Ocelot
 * @since 2.4.0
 */
public interface AdvancedFboTextureAttachment extends AdvancedFboAttachment
{
    @Override
    default void attach(int target, int attachment)
    {
        for (int i = 0; i < this.getMipmapLevels(); i++)
            this.attach(target, attachment, i);
    }

    /**
     * Attaches this attachment to the provided target under the specified attachment id.
     *
     * @param target     The target to attach this attachment to
     * @param attachment The attachment to attach this attachment under
     * @param level      The mipmap level to attach
     */
    void attach(int target, int attachment, int level);

    /**
     * @return The mipmap levels in this attachment
     */
    int getMipmapLevels();

    @Override
    AdvancedFboTextureAttachment createCopy();
}
