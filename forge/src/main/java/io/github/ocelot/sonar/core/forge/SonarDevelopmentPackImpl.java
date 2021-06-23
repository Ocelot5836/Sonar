package io.github.ocelot.sonar.core.forge;

import io.github.ocelot.sonar.Sonar;
import io.github.ocelot.sonar.core.SonarDevelopmentPack;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SonarDevelopmentPackImpl
{
    public static void init()
    {
        if (FMLLoader.isProduction() || Minecraft.getInstance() == null)
            return;
        Minecraft.getInstance().getResourcePackRepository().addPackFinder((packs, packInfoFactory) ->
        {
            Pack t1 = Pack.create(Sonar.DOMAIN + "_dev", true, SonarDevelopmentPack::new, packInfoFactory, Pack.Position.TOP, PackSource.BUILT_IN);
            if (t1 != null)
            {
                packs.accept(t1);
            }
        });
    }
}
