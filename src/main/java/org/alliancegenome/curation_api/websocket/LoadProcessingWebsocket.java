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

import org.alliancegenome.curation_api.model.event.ProcessingEvent;

import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/processing_events")
@ApplicationScoped
public class LoadProcessingWebsocket {

	@Inject
	ObjectMapper mapper;

	Map<String, Session> sessions = new ConcurrentHashMap<>();

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
		for (Entry<String, Session> sessionEntry : sessions.entrySet()) {
			try {
				sessionEntry.getValue().getAsyncRemote().sendText(mapper.writeValueAsString(event));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
