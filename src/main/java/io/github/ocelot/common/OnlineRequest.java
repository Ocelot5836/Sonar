package io.github.ocelot.common;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * <p>An asynchronous way to make requests to the internet.</p>
 * <p>{@link #make(String, Consumer, Consumer)} can be used to make a request and execute a callback when the data is received.</p>
 * <p>An alternate option is to use {@link #make(String, Consumer)} which returns a {@link Future} that will contains the data at some point in the future. In order to wait until the data is downloaded, use <code>result = make(url).get()</code></p>
 *
 * @author Ocelot
 * @see Consumer
 * @see Future
 * @since 2.0.0
 */
public class OnlineRequest
{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), task -> new Thread(task, "Online Request Pool"));

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(POOL::shutdown));
    }

    private OnlineRequest() {}

    private static InputStream request(String url) throws IOException
    {
        try (CloseableHttpClient client = HttpClients.createDefault())
        {
            HttpGet get = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(get))
            {
                return IOUtils.toBufferedInputStream(response.getEntity().getContent());
            }
        }
    }

    /**
     * Adds a request to the queue. Use the callback to process the response fetched from the URL connection.
     *
     * @param url           the URL to make a request to
     * @param callback      the response callback for the request
     * @param errorCallback The callback to use when an error occurs or null to ignore errors
     */
    public static void make(String url, Consumer<InputStream> callback, @Nullable Consumer<IOException> errorCallback)
    {
        POOL.execute(() ->
        {
            try
            {
                callback.accept(request(url));
            }
            catch (IOException e)
            {
                if (errorCallback != null)
                {
                    errorCallback.accept(e);
                }
                else
                {
                    callback.accept(null);
                }
            }
        });
    }

    /**
     * Adds a request to the queue.
     *
     * @param url           the URL to make a request to
     * @param errorCallback The callback to use when an error occurs or null to ignore errors
     * @return A {@link Future} representing the resulting task of this.
     */
    public static Future<InputStream> make(String url, @Nullable Consumer<IOException> errorCallback)
    {
        return POOL.submit(() ->
        {
            try
            {
                return request(url);
            }
            catch (IOException e)
            {
                if (errorCallback != null)
                    errorCallback.accept(e);
                return null;
            }
        });
    }
}
