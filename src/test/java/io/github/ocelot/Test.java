package io.github.ocelot;

import io.github.ocelot.common.OnlineRequest;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        try (OnlineRequest.Request request = OnlineRequest.make("http://ipv4.download.thinkbroadband.com/50MB.zip", null, null))
        {
            double currentProgress = -1;
            while (!request.getValue().isPresent())
            {
                if (currentProgress != request.getDownloadPercentage())
                {
                    System.out.println(currentProgress = request.getDownloadPercentage());
                }
            }
            System.out.println("Download took " + (System.currentTimeMillis() - request.getStartTime()) + "ms");
            try (FileOutputStream os = new FileOutputStream("50MB.zip"))
            {
                IOUtils.copy(request.getValue().get(), os);
            }
        }
        System.exit(0);
    }
}
