package com.eron.challenge.ext.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Page envelope of the external API for movies search. Provides totalPages to
 * drive pagination.
 */
public record MoviesPage(int page, @JsonProperty("per_page") int perPage, int total,
		@JsonProperty("total_pages") int totalPages, List<Movie> data)
{
}
