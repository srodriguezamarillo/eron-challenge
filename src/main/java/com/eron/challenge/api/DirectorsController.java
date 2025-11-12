package com.eron.challenge.api;

import com.eron.challenge.core.DirectorsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * REST controller exposing a single read endpoint to list directors whose movie
 * count is strictly greater than the given threshold. Keeps the controller
 * thin: input validation + delegation to the service.
 */
@Validated
@RestController
@RequestMapping(path = "/api/directors", produces = MediaType.APPLICATION_JSON_VALUE)
public class DirectorsController
{

	private final DirectorsService service;

	/**
	 * Constructor-based injection for explicit, testable dependencies.
	 */
	public DirectorsController(DirectorsService service)
	{
		this.service = service;
	}

	/**
	 * GET /api/directors?threshold=X Returns {"directors":[...]} sorted
	 * alphabetically, case-insensitive. Responds 400 if threshold is negative,
	 * without invoking the service.
	 */
	@GetMapping
	public ResponseEntity<Map<String, List<String>>> getDirectors(@RequestParam int threshold)
	{
		if (threshold < 0)
		{
			return ResponseEntity.badRequest().body(Map.of("directors", List.of()));
		}
		List<String> result = service.directorsWithMoreThan(threshold);
		return ResponseEntity.ok(Map.of("directors", result));
	}
}
