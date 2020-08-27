package io.github.ocelot;

import io.github.ocelot.common.OnlineRequest;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Test
{
    private static long runPhase(boolean async)
    {
        long startTime = System.currentTimeMillis();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < 16; i++)
            futures.add(OnlineRequest.request("https://cdn.discordapp.com/attachments/683471388434366490/748648773659656284/unknown.png"));
        if (async)
        {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        else
        {
            for (CompletableFuture<?> future : futures)
                future.join();
        }
        return System.currentTimeMillis() - startTime;
    }

    public static void main(String[] args)
    {
        System.out.println("Took " + runPhase(false) + "ms");
    }
}
