package com.eron.challenge.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

/**
 * Centralized error mapping producing compact, stable JSON payloads. Avoids
 * leaking stack traces and aligns upstream failures to 502.
 */
@ControllerAdvice
public class GlobalExceptionHandler
{

	/**
	 * Maps explicit ResponseStatusException to a concise bad_request payload.
	 */
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex)
	{
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		String message = Optional.ofNullable(ex.getReason()).orElse("Bad request");
		return ResponseEntity.status(status != null ? status : HttpStatus.BAD_REQUEST)
				.body(Map.of("error", "bad_request", "message", message));
	}

	/**
	 * Maps client/network errors against the external API to 502 Bad Gateway.
	 */
	@ExceptionHandler(RestClientException.class)
	public ResponseEntity<Map<String, Object>> handleRestClient(RestClientException ex)
	{
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
				.body(Map.of("error", "upstream_failure", "message", "External Movies API request failed"));
	}

	/**
	 * Last-resort mapping for unexpected exceptions.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex)
	{
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "internal_error", "message", "Unexpected server error"));
	}
}
