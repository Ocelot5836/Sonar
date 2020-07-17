package io.github.ocelot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 16; ++i)
        {
            for (int j = 0; j < 16; ++j)
            {
                if (i < 8)
                {
                    image.setRGB(j, i, 0xFFB20000);
                }
                else
                {
                    int k = (int) ((1.0F - (float) j / 15.0F * 0.75F) * 255.0F);
                    image.setRGB(j, i, k << 24 | 16777215);
                }
            }
        }
        ImageIO.write(image, "PNG", new File("overlay.png"));
    }
}
