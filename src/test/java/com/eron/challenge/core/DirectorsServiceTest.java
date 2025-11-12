package com.eron.challenge.core;

import com.eron.challenge.ext.MovieApiClient;
import com.eron.challenge.ext.dto.Movie;
import com.eron.challenge.ext.dto.MoviesPage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure unit test for the business logic using a fake API client. Exercises
 * pagination, counting, strict filtering and sorting.
 */
class DirectorsServiceTest
{

	static class FakeApiClient extends MovieApiClient
	{
		FakeApiClient()
		{
			super("http://localhost", 10, 10, 0);
		}

		@Override
		public MoviesPage fetchPage(int page)
		{
			// Two pages, crafted to exercise counting & strict threshold
			if (page == 1)
			{
				return new MoviesPage(1, 2, 4, 2,
						List.of(new Movie("A", "2011", "Woody Allen"), new Movie("B", "2012", "Martin Scorsese")));
			}
			else if (page == 2)
			{
				return new MoviesPage(2, 2, 4, 2,
						List.of(new Movie("C", "2013", "Woody Allen"), new Movie("D", "2014", "Woody Allen")));
			}
			throw new IllegalArgumentException("Unexpected page " + page);
		}
	}

	@Test
	void directorsWithMoreThan_filtersAndSortsProperly()
	{
		DirectorsService service = new DirectorsService(new FakeApiClient());

		// Woody = 3, Scorsese = 1
		assertEquals(List.of("Woody Allen"), service.directorsWithMoreThan(2));
		assertEquals(List.of("Martin Scorsese", "Woody Allen"), service.directorsWithMoreThan(0));
		assertEquals(List.of(), service.directorsWithMoreThan(5));
	}
}
