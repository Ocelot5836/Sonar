/*******************************************************************************
 * Copyright 2019 Brandon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.ocelot.api.utils;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 
 * Requests information from the Internet asynchronously.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 *
 */
public class OnlineRequest
{
	/** The queue of requests being sent. */
	private static final ExecutorService REQUEST_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	/** The queue of requests being handled. */
	private static final ExecutorService WAIT_POOL = Executors.newSingleThreadExecutor();

	static
	{
		Runtime.getRuntime().addShutdownHook(new Thread(() ->
		{
			REQUEST_POOL.shutdown();
			WAIT_POOL.shutdown();
		}));
	}

	/**
	 * Processes the url passed in.
	 * 
	 * @param future The future to update
	 * @param url    The url to load the data from
	 */
	private static void process(CompletableFuture future, URI url)
	{
		try (CloseableHttpClient client = HttpClients.createDefault())
		{
			HttpGet get = new HttpGet(url);
			try (CloseableHttpResponse response = client.execute(get))
			{
				future.complete(IOUtils.toBufferedInputStream(response.getEntity().getContent()));
			}
		}
		catch (Throwable t)
		{
			future.completeExceptionally(t);
		}
	}

	/**
	 * Makes a request to the specified URL.
	 * 
	 * @param url The URL to request the data from
	 * @return The data after it has been downloaded
	 */
	public static Future<InputStream> make(String url)
	{
		try
		{
			return make(new URI(url));
		}
		catch (URISyntaxException e)
		{
			CompletableFuture<InputStream> future = new CompletableFuture<InputStream>();
			future.completeExceptionally(e);
			return future;
		}
	}

	/**
	 * Makes a request to the specified URL.
	 * 
	 * @param url The URL to request the data from
	 * @return The data after it has been downloaded
	 */
	public static Future<InputStream> make(URI url)
	{
		CompletableFuture<InputStream> future = new CompletableFuture<InputStream>();
		REQUEST_POOL.execute(() -> process(future, url));
		return future;
	}

	/**
	 * Makes a request to the specified URL.
	 * 
	 * @param url      The URL to request the data from
	 * @param callback The callback that will be executed once the data has been
	 *                 downloaded. The stream will be null if it could not be
	 *                 downloaded.
	 */
	public static void make(String url, Consumer<InputStream> callback)
	{
		WAIT_POOL.execute(() ->
		{
			try
			{
				callback.accept(make(url).get());
			}
			catch (Exception e)
			{
				callback.accept(null);
			}
		});
	}

	/**
	 * Makes a request to the specified URL.
	 * 
	 * @param url      The URL to request the data from
	 * @param callback The callback that will be executed once the data has been
	 *                 downloaded. The stream will be null if it could not be
	 *                 downloaded.
	 */
	public static void make(URI url, Consumer<InputStream> callback)
	{
		WAIT_POOL.execute(() ->
		{
			try
			{
				callback.accept(make(url).get());
			}
			catch (Exception e)
			{
				callback.accept(null);
			}
		});
	}
}
