package com.eron.challenge.core;

import com.eron.challenge.ext.MovieApiClient;
import com.eron.challenge.ext.dto.Movie;
import com.eron.challenge.ext.dto.MoviesPage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Core business logic: fetches all pages, counts movies per director, filters
 * strictly greater than threshold, and sorts names alphabetically.
 */
@Service
public class DirectorsService
{

	private final MovieApiClient client;

	/**
	 * The service depends on the external API client only.
	 */
	public DirectorsService(MovieApiClient client)
	{
		this.client = client;
	}

	/**
	 * Computes director names with movie count strictly greater than threshold.
	 * Aggregation is null-safe and trimming removes accidental blanks.
	 */
	public List<String> directorsWithMoreThan(int threshold)
	{
		MoviesPage first = client.fetchPage(1);
		int totalPages = first.totalPages();

		List<Movie> all = new ArrayList<>();
		if (first.data() != null) all.addAll(first.data());

		if (totalPages > 1)
		{
			IntStream.rangeClosed(2, totalPages).mapToObj(client::fetchPage).map(MoviesPage::data)
					.filter(Objects::nonNull).forEach(all::addAll);
		}

		Map<String, Long> counts = all.stream().map(Movie::director).filter(Objects::nonNull).map(String::trim)
				.filter(s -> !s.isEmpty()).collect(Collectors.groupingBy(d -> d, Collectors.counting()));

		return counts.entrySet().stream().filter(e -> e.getValue() > threshold).map(Map.Entry::getKey)
				.sorted(String::compareToIgnoreCase).toList();
	}
}
