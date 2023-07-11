package org.alliancegenome.curation_api.controllers;

import java.util.Map;

import org.alliancegenome.curation_api.dao.base.SystemSQLDAO;
import org.alliancegenome.curation_api.interfaces.SystemControllerInterface;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SystemController implements SystemControllerInterface {

	@Inject
	SystemSQLDAO systemSQLDAO;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	
	@Override
	public void reindexEverything(Integer threadsToLoadObjects, Integer typesToIndexInParallel, Integer limitIndexedObjectsTo, Integer batchSizeToLoadObjects, Integer idFetchSize,
		Integer transactionTimeout) {
		systemSQLDAO.reindexEverything(threadsToLoadObjects, typesToIndexInParallel, limitIndexedObjectsTo, batchSizeToLoadObjects, idFetchSize, transactionTimeout);
	}

	@Override
	public ObjectResponse<Map<String, Object>> getSiteSummary() {
		return systemSQLDAO.getSiteSummary();
	}

	// TODO remove once SCRUM-3037 resolved
	public void resetDiseaseAnnotationDataProviders() {
		diseaseAnnotationService.resetDataProviders();
		diseaseAnnotationService.cleanUpDataProviders();
	}
	
	public void updateDiseaseAnnotationUniqueIds() {
		diseaseAnnotationService.updateUniqueIds();
	}

	@Override
	public void updateRefreshIntervalOnAllIndexes() {
		systemSQLDAO.setRefreshInterval();
	}
}
