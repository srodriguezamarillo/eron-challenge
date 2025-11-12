package com.eron.challenge.api;

import com.eron.challenge.core.DirectorsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Standalone MVC test for the controller: fast and deterministic. Covers happy
 * path and negative-threshold guard.
 */
class DirectorsControllerTest
{

	private MockMvc mvc;
	private DirectorsService service;

	@BeforeEach
	void setUp()
	{
		service = Mockito.mock(DirectorsService.class);
		DirectorsController controller = new DirectorsController(service);
		// No ControllerAdvice necesario para este caso
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	void returnsAlphabeticalDirectorsStrictlyGreaterThanThreshold() throws Exception
	{
		when(service.directorsWithMoreThan(4)).thenReturn(List.of("Martin Scorsese", "Woody Allen"));

		mvc.perform(get("/api/directors").param("threshold", "4").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.directors[0]").value("Martin Scorsese"))
				.andExpect(jsonPath("$.directors[1]").value("Woody Allen"));

		verify(service).directorsWithMoreThan(4);
	}

	@Test
	void rejectsNegativeThreshold() throws Exception
	{
		mvc.perform(get("/api/directors").param("threshold", "-1")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.directors").isArray()).andExpect(jsonPath("$.directors").isEmpty());

		// No debe invocar la capa de servicio en caso inválido
		verify(service, never()).directorsWithMoreThan(anyInt());
	}
}
