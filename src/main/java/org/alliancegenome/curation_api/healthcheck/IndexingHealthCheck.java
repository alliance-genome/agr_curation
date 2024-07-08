package org.alliancegenome.curation_api.healthcheck;

import org.alliancegenome.curation_api.model.event.index.EndIndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.IndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.ProgressIndexProcessingEvent;
import org.alliancegenome.curation_api.model.event.index.StartIndexProcessingEvent;
import org.alliancegenome.curation_api.websocket.IndexProcessingWebsocket;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Liveness
@ApplicationScoped
public class IndexingHealthCheck implements HealthCheck {

	@Inject ObjectMapper mapper;
	@Inject IndexProcessingWebsocket indexProcessingWebsocket;

	@Override
	public HealthCheckResponse call() {

		HealthCheckResponseBuilder resp = null;

		resp = HealthCheckResponse.named("Elasticsearch Indexing health check").up();

		try {
			IndexProcessingEvent event = indexProcessingWebsocket.getEvent();

			if (event != null) {
				resp.withData("json", mapper.writeValueAsString(event));

				if (event instanceof StartIndexProcessingEvent) {
					resp.withData("status", "Start Indexing");
				}
				if (event instanceof ProgressIndexProcessingEvent) {
					resp.withData("status", "Indexing");
				}
				if (event instanceof EndIndexProcessingEvent) {
					resp.withData("status", "Indexing Finished");
				}
			}
		} catch (JsonProcessingException e) {
			resp.withData("status", "Error: " + e.getMessage());
		}

		return resp.build();
	}
}
