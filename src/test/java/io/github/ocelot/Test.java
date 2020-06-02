package io.github.ocelot;

import io.github.ocelot.common.OnlineRequest;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        OnlineRequest.Request request = OnlineRequest.make("http://ipv4.download.thinkbroadband.com/1GB.zip", stream -> {
            try
            {
                IOUtils.toBufferedInputStream(stream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace);
        Thread.sleep(4000);
        request.cancel();
//            OnlineRequest.Request request = OnlineRequest.make("http://ipv4.download.thinkbroadband.com/50MB.zip", null, Throwable::printStackTrace)
//            double currentProgress = -1;
//            while (!request.isCancelled() && !request.getValue().isPresent())
//            {
//                if (currentProgress != request.getDownloadPercentage())
//                {
//                    System.out.println(currentProgress = request.getDownloadPercentage());
//                }
//                if (currentProgress >= 0.1)
//                {
//                    System.out.println("Cancelling");
//                    request.cancel();
//                }
//            }
//            if (request.isCancelled())
//            {
//                System.out.println("Download was cancelled");
//            }
//            else
//            {
//                System.out.println("Download took " + (System.currentTimeMillis() - request.getStartTime()) + "ms");
//                try (FileOutputStream os = new FileOutputStream("50MB.zip"))
//                {
//                    IOUtils.copy(request.getValue().get(), os);
//                }
//            }
    }
}
