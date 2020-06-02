package io.github.ocelot.common;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
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
 * <p>{@link #make(String, Consumer, Consumer)} can be used to make a request and return a {@link Request} with the current status of the request.</p>
 *
 * @author Ocelot
 * @see Consumer
 * @see Future
 * @since 2.0.0
 */
public class OnlineRequest
{
    private static final ExecutorService POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), task -> new Thread(task, "Online Request Pool"));
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    static
    {
        Runtime.getRuntime().addShutdownHook(new Thread(POOL::shutdown));
    }

    private OnlineRequest()
    {
    }

    private static void request(Request request) throws Exception
    {
        if (request.isCancelled())
            return;

        HttpGet get = new HttpGet(request.getUrl());
        try (CloseableHttpClient client = HttpClients.custom().setUserAgent(USER_AGENT).build())
        {
            try (CloseableHttpResponse response = client.execute(get))
            {
                String contentLength = response.getFirstHeader("Content-Length").getValue();
                if (contentLength != null)
                    request.setFileSize(Long.parseLong(contentLength));
                try (CountingInputStream countingInputStream = new CountingInputStream(response.getEntity().getContent())
                {
                    @Override
                    public synchronized long skip(long length) throws IOException
                    {
                        long skip = super.skip(length);
                        request.setReceived(this.getByteCount());
                        if (request.isCancelled())
                            this.close();
                        return skip;
                    }

                    @Override
                    protected synchronized void afterRead(int n)
                    {
                        super.afterRead(n);
                        request.setReceived(this.getByteCount());
                    }
                })
                {
                    request.setValue(countingInputStream);
                }
            }
        }
        finally
        {
            get.releaseConnection();
        }
    }

    /**
     * <p>Adds a new request to the queue.</p>
     * <p>The supplied callback will be called when a result is received and no error is thrown.</p>
     * <p>If the stream needs to be saved for later, use {@link IOUtils#toBufferedInputStream(InputStream)} to make a copy. The stream <b><i>WILL NOT PERSIST AFTER THE CALLBACK IS CALLED!</i></b></p>
     * <p>If there is no error callback and an error is thrown <code>null</code> is passed into the callback if not null.</p>
     * <p>If the error callback is not null and an error is thrown, the callback <b><i>WILL NOT BE CALLED</i></b> and the exception will be passed into the error callback.</p>
     *
     * @param url           the URL to make a request to
     * @param callback      the response callback for the request
     * @param errorCallback The callback to use when an error occurs or null to ignore errors
     * @return The result and statistics about the request
     */
    public static Request make(String url, Consumer<InputStream> callback, @Nullable Consumer<Exception> errorCallback)
    {
        Request request = new Request(url, callback);
        POOL.execute(() ->
        {
            try
            {
                request(request);
            }
            catch (Exception e)
            {
                if (errorCallback != null)
                {
                    errorCallback.accept(e);
                }
                else
                {
                    request.setValue(null);
                }
            }
        });
        return request;
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

    /**
     * <p>A request made to the internet that contains request stats and progress.</p>
     *
     * @author Ocelot
     * @since 3.0.0
     */
    public static class Request
    {
        private final String url;
        private final Consumer<InputStream> listener;
        private volatile long fileSize;
        private volatile long bytesReceived;
        private volatile long startTime;
        private boolean cancelled;
        private volatile boolean stopped;

        private Request(String url, Consumer<InputStream> listener)
        {
            this.url = url;
            this.listener = listener;
            this.fileSize = 0;
            this.bytesReceived = 0;
            this.startTime = 0;
            this.cancelled = false;
            this.stopped = true;
        }

        /**
         * Cancels the HTTP operation before is starts or cancels byte reading if already being processed.
         */
        public void cancel()
        {
            this.cancelled = true;
        }

        /**
         * @return The url the request was made to
         */
        public String getUrl()
        {
            return url;
        }

        /**
         * @return The total size of the file being downloaded or 0 if it has not been set yet
         */
        public long getFileSize()
        {
            return fileSize;
        }

        /**
         * @return The amount of bytes received
         */
        public long getBytesReceived()
        {
            return bytesReceived;
        }

        /**
         * @return A percentage of how complete the download is
         */
        public double getDownloadPercentage()
        {
            return this.fileSize > 0 ? (double) this.bytesReceived / (double) this.fileSize : 0;
        }

        /**
         * @return The time the download started
         */
        public long getStartTime()
        {
            return startTime;
        }

        /**
         * @return Whether or not all bytes have been read from the download
         */
        public boolean isDownloaded()
        {
            return this.fileSize > 0 && this.bytesReceived >= this.fileSize;
        }

        /**
         * @return Whether or not this operation has been cancelled
         */
        public boolean isCancelled()
        {
            return cancelled;
        }

        /* Internal methods */

        private synchronized void setFileSize(long fileSize)
        {
            this.fileSize = fileSize;
        }

        private synchronized void setReceived(long bytesReceived)
        {
            this.bytesReceived = bytesReceived;
            if (this.startTime == 0)
                this.startTime = System.nanoTime();
        }

        private synchronized void setValue(InputStream stream)
        {
            this.listener.accept(stream);
        }
    }
}
