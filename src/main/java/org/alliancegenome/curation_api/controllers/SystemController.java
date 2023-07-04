package org.alliancegenome.curation_api.controllers;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.base.SystemSQLDAO;
import org.alliancegenome.curation_api.interfaces.SystemControllerInterface;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;

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
	}
}
