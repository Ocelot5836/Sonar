package io.github.ocelot.sonar.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.EofSensorInputStream;
import org.apache.http.conn.EofSensorWatcher;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * <p>An asynchronous way to make requests to the internet.</p>
 * <p>{@link #get(String)} can be used to open a new stream to the internet. <b><i>NOTE: THIS STREAM CANNOT BE KEPT OPEN AND IS NOT OFF-THREAD!</i></b></p>
 * <p>{@link #request(String)} and {@link #request(String, Executor)} can be used instead to fetch all data on another thread.</p>
 *
 * @author Ocelot
 * @see CompletableFuture
 * @since 2.0.0
 */
public class OnlineRequest
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is not asynchronous and will block code execution until the value has been received.</p>
     *
     * @param url The url to get the data from
     * @return An open stream to the internet
     */
    public static InputStream get(String url) throws IOException
    {
        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build();
        CloseableHttpResponse response = client.execute(get);
        StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != 200)
        {
            client.close();
            response.close();
            throw new IOException("Failed to connect to '" + url + "'. " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        }
        return new EofSensorInputStream(response.getEntity().getContent(), new EofSensorWatcher()
        {
            @Override
            public boolean eofDetected(InputStream wrapped) throws IOException
            {
                response.close();
                return true;
            }

            @Override
            public boolean streamClosed(InputStream wrapped) throws IOException
            {
                response.close();
                return true;
            }

            @Override
            public boolean streamAbort(InputStream wrapped) throws IOException
            {
                response.close();
                return true;
            }
        });
    }

    /**
     * <p>Fetches data from the specified url on the specified executor.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url      The url to get the data from
     * @param executor The executor to run the request on
     * @return A copy of the data read from the specified URL
     */
    public static CompletableFuture<InputStream> request(String url, Executor executor)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try (InputStream stream = get(url))
            {
                return IOUtils.toBufferedInputStream(stream);
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to fully read stream from '" + url + "'", e);
                return null;
            }
        }, executor);
    }

    /**
     * <p>Fetches data from the specified url.</p>
     * <p>This method is asynchronous and the received value is indicated to exist at some point in the future.</p>
     *
     * @param url The url to get the data from
     * @return A copy of the data read from the specified URL
     */
    public static CompletableFuture<InputStream> request(String url)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try (InputStream stream = get(url))
            {
                return IOUtils.toBufferedInputStream(stream);
            }
            catch (Exception e)
            {
                LOGGER.error("Failed to fully read stream from '" + url + "'", e);
                return null;
            }
        });
    }

    /**
     * Sets the user agent to use when making online requests.
     *
     * @param userAgent The new user agent
     */
    public static void setUserAgent(String userAgent)
    {
        USER_AGENT = userAgent;
    }
}
