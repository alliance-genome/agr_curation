package org.alliancegenome.curation_api.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

@ApplicationScoped
@ServerEndpoint("/websockets/processing_events")
public class LoadProcessingWebsocket {
	
	@Inject ObjectMapper mapper;

	private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

	@OnOpen
	public void onOpen(Session session) {
		try {
			sessions.add(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void onClose(Session session) {
		try {
			sessions.remove(session);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@OnError
	public void onError(Session session) {
		sessions.remove(session);
	}

	@OnMessage
	public void message(String message, Session session) {
		// session.getOpenSessions().forEach(s -> s.getAsyncRemote().sendText(message));
	}

	public void observeProcessingEvent(@Observes ProcessingEvent event) {
		for (Session session : sessions) {
			try {
				session.getAsyncRemote().sendText(mapper.writeValueAsString(event));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
