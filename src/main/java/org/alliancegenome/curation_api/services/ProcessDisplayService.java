package org.alliancegenome.curation_api.services;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.alliancegenome.curation_api.model.event.EndProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProgressProcessingEvent;
import org.alliancegenome.curation_api.model.event.StartProcessingEvent;
import org.alliancegenome.curation_api.util.ProcessDisplayHandler;

@ApplicationScoped
public class ProcessDisplayService implements ProcessDisplayHandler {

	@Inject
	Event<ProcessingEvent> processingEvent;

	@Override
	public void startProcess(String message, long startTime, long totalSize) {
		processingEvent.fire(new StartProcessingEvent(message, startTime, totalSize));
	}

	@Override
	public void progressProcess(String message, String data, long startTime, long nowTime, long lastTime, long currentCount, long lastCount, long totalSize) {
		processingEvent.fire(new ProgressProcessingEvent(message, data, startTime, nowTime, lastTime, currentCount, lastCount, totalSize));
	}

	@Override
	public void finishProcess(String message, String data, long current, long totalSize, long duration) {
		processingEvent.fire(new EndProcessingEvent(message, data, current, totalSize, duration));
	}
}
