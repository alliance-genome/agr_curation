package org.alliancegenome.curation_api.jobs.executors;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.event.EndProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProcessingEvent;
import org.alliancegenome.curation_api.model.event.ProgressProcessingEvent;
import org.alliancegenome.curation_api.model.event.StartProcessingEvent;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.util.ProcessDisplayHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadFileExecutor implements ProcessDisplayHandler {
	
	@Inject Event<ProcessingEvent> processingEvent;
	@Inject ObjectMapper mapper;
	@Inject BulkLoadFileDAO bulkLoadFileDAO;
	@Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	
	protected void trackHistory(APIResponse runHistory, BulkLoadFile bulkLoadFile) {
		LoadHistoryResponce res = (LoadHistoryResponce)runHistory;
		BulkLoadFileHistory history = res.getHistory();
		
		history.setBulkLoadFile(bulkLoadFile);
		bulkLoadFileHistoryDAO.persist(history);
		
		for(BulkLoadFileException e: history.getExceptions()) {
			bulkLoadFileExceptionDAO.persist(e);
		}
		
		bulkLoadFile.getHistory().add(history);
		bulkLoadFileDAO.merge(bulkLoadFile);
		
	}

	protected void addException(BulkLoadFileHistory history, ObjectUpdateExceptionData objectUpdateExceptionData) {
		BulkLoadFileException exception = new BulkLoadFileException();
		exception.setException(objectUpdateExceptionData);
		exception.setBulkLoadFileHistory(history);
		history.getExceptions().add(exception);
		history.incrementFailed();
	}
	
	protected String getVersionNumber (String versionString) {
		if (versionString.startsWith("v"))
			return versionString.substring(1);
		return versionString;
	}
	
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
