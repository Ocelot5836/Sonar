package io.github.ocelot.sonar.client.screen.forge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ValueContainerEditorScreenImpl
{
    public static boolean isActiveAndMatches(KeyMapping mapping, InputConstants.Key keyCode)
    {
        return mapping.isActiveAndMatches(keyCode);
    }
}
