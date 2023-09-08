package org.alliancegenome.curation_api.websocket;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.alliancegenome.curation_api.model.event.EndProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProgressProcessingEvent;
import org.alliancegenome.curation_api.model.event.StartProcessingEvent;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Liveness
@ServerEndpoint("/processing_events")
@ApplicationScoped
public class LoadProcessingWebsocket implements HealthCheck {

	@Inject
	ObjectMapper mapper;

	Map<String, Session> sessions = new ConcurrentHashMap<>();
	
	private ProcessingEvent event;

	@OnOpen
	public void onOpen(Session session) {
		try {
			sessions.put(session.getId(), session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session) {
		try {
			sessions.remove(session.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		sessions.remove(session.getId());
	}

	@OnMessage
	public void message(String message, Session session) {
		// session.getOpenSessions().forEach(s -> s.getAsyncRemote().sendText(message));
	}

	public void observeProcessingEvent(@Observes ProcessingEvent event) {
		this.event = event;
		for (Entry<String, Session> sessionEntry : sessions.entrySet()) {
			try {
				sessionEntry.getValue().getAsyncRemote().sendText(mapper.writeValueAsString(event));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public HealthCheckResponse call() {

		HealthCheckResponseBuilder resp = null;
		
		resp = HealthCheckResponse.named("Elasticsearch Indexing health check").up();
		
		try {
			if(event != null) {
				resp.withData("json", mapper.writeValueAsString(event));
				
				if(event instanceof StartProcessingEvent event) {
					resp.withData("status", "Start Indexing");
				}
				if(event instanceof ProgressProcessingEvent event) {
					resp.withData("status", "Indexing");
				}
				if(event instanceof EndProcessingEvent event) {
					resp.withData("status", "Indexing Finished");
				}
			}
		} catch (JsonProcessingException e) {
			resp.withData("status", "Error: " + e.getMessage());
		}

		return resp.build();
	}
	
}
