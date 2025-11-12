package com.eron.challenge.ext;

import com.eron.challenge.ext.dto.MoviesPage;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies client-side parsing against a controlled HTTP server. Ensures JSON
 * fields map to DTOs as expected.
 */
class MovieApiClientTest
{

	private MockWebServer server;
	private MovieApiClient client;

	@BeforeEach
	void setUp() throws Exception
	{
		server = new MockWebServer();
		server.start();

		// Base URL must include the path used by the client
		String baseUrl = server.url("/api/movies/search").toString();
		client = new MovieApiClient(baseUrl, 500, 1000, 0);
	}

	@AfterEach
	void tearDown() throws Exception
	{
		server.shutdown();
	}

	@Test
	void fetchPage_parsesPayload()
	{
		String body = """
				{
				  "page": 1,
				  "per_page": 2,
				  "total": 3,
				  "total_pages": 2,
				  "data": [
				    { "Title": "Film1", "Year": "2011", "Director": "A" },
				    { "Title": "Film2", "Year": "2012", "Director": "B" }
				  ]
				}
				""";

		server.enqueue(
				new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json").setBody(body));

		MoviesPage page = client.fetchPage(1);

		assertEquals(1, page.page());
		assertEquals(2, page.perPage());
		assertEquals(3, page.total());
		assertEquals(2, page.totalPages());
		assertEquals(2, page.data().size());
		assertEquals("Film1", page.data().get(0).title());
		assertEquals("A", page.data().get(0).director());
	}
}
