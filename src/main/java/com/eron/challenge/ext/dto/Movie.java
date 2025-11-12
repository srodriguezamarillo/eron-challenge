package com.eron.challenge.ext.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal fields consumed by the business layer. Matches external payload keys
 * via Jackson annotations.
 */
public record Movie(@JsonProperty("Title") String title, @JsonProperty("Year") String year,
		@JsonProperty("Director") String director)
{
}
