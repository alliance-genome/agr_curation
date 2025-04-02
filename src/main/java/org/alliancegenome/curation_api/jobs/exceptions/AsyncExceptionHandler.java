package org.alliancegenome.curation_api.jobs.exceptions;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.jobs.events.StartedLoadJobEvent;
import org.alliancegenome.curation_api.jobs.processors.BulkLoadFMSProcessor;
import org.alliancegenome.curation_api.jobs.processors.BulkLoadURLProcessor;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkFMSLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkURLLoad;

import io.quarkus.arc.AsyncObserverExceptionHandler;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.EventContext;
import jakarta.enterprise.inject.spi.ObserverMethod;
import jakarta.inject.Inject;

@ApplicationScoped
public class AsyncExceptionHandler implements AsyncObserverExceptionHandler {

	@Inject BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject BulkLoadFMSProcessor bulkLoadFMSProcessor;
	@Inject BulkLoadURLProcessor bulkLoadURLProcessor;
	
	@Override
	public void handle(Throwable throwable, ObserverMethod<?> observerMethod, EventContext<?> eventContext) {

		if(eventContext.getEvent() instanceof StartedLoadJobEvent) {
			StartedLoadJobEvent event = (StartedLoadJobEvent)eventContext.getEvent();
			BulkLoadFileHistory bulkLoadFileHistory = bulkLoadFileHistoryDAO.find(event.getId());

			if(bulkLoadFileHistory.getBulkLoad() instanceof BulkFMSLoad bulkFMSLoad) {
				bulkLoadFMSProcessor.endLoad(bulkLoadFileHistory, "Failed loading: " + bulkFMSLoad.getName() + " please check the logs for more info. " + bulkLoadFileHistory.getErrorMessage(), JobStatus.FAILED);
			}
			
			if(bulkLoadFileHistory.getBulkLoad() instanceof BulkURLLoad bulkURLLoad) {
				bulkLoadURLProcessor.endLoad(bulkLoadFileHistory, "Failed loading: " + bulkURLLoad.getName() + " please check the logs for more info. " + bulkLoadFileHistory.getErrorMessage(), JobStatus.FAILED);
			}

			Log.error("Load File: " + bulkLoadFileHistory.getBulkLoad().getName() + " is failed");
			throwable.printStackTrace();

		} else {
			Log.error("Error handling missing for error type: " + observerMethod);
		}
		
	}

}


