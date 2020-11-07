package io.github.ocelot.sonar.network;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TestClientHandler
{
    public TestClientHandler()
    {
        System.out.println("CREATING NEW CLIENT HANDLER");
    }
}
