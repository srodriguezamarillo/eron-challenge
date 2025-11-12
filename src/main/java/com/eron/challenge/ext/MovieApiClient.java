package com.eron.challenge.ext;

import com.eron.challenge.ext.dto.MoviesPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Thin HTTP client around the external Movies API. Uses blocking RestClient to
 * match Spring MVC threading model. Timeouts and maxRetries are configurable
 * via application.yaml.
 */
@Component
public class MovieApiClient
{

	private final RestClient rest;
	private final int maxRetries;

	/**
	 * Builds a RestClient with explicit connect/read timeouts.
	 */
	public MovieApiClient(@Value("${app.moviesApi.baseUrl}") String baseUrl,
			@Value("${app.http.connectTimeoutMs}") int connectTimeoutMs,
			@Value("${app.http.readTimeoutMs}") int readTimeoutMs,
			@Value("${app.http.maxRetries}") int maxRetries)
	{
		this.maxRetries = Math.max(0, maxRetries);

		// Simple blocking HTTP client with timeouts
		SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
		rf.setConnectTimeout((int) Duration.ofMillis(connectTimeoutMs).toMillis());
		rf.setReadTimeout((int) Duration.ofMillis(readTimeoutMs).toMillis());

		this.rest = RestClient.builder().baseUrl(baseUrl).requestFactory(rf).build();
	}

	/**
	 * Fetches one page (1-based) with an optional minimal retry loop.
	 */
	public MoviesPage fetchPage(int page)
	{
		int attempt = 0;
		RuntimeException last = null;

		// Minimal retry loop (if configured)
		while (attempt <= maxRetries)
		{
			try
			{
				return rest.get().uri(uri -> uri.queryParam("page", page).build()).retrieve()
						.body(MoviesPage.class);
			}
			catch (RuntimeException ex)
			{
				last = ex;
				attempt++;
				if (attempt > maxRetries) throw ex;
				try
				{
					Thread.sleep(300);
				}
				catch (InterruptedException ie)
				{
					Thread.currentThread().interrupt();
					throw new RuntimeException("Interrupted during retry", ie);
				}
			}
		}
		throw last;  // defensive; unreachable
	}
}
