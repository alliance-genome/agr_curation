package org.alliancegenome.curation_api.controllers;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.SystemSQLDAO;
import org.alliancegenome.curation_api.interfaces.SystemControllerInterface;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.ConditionRelationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.ExperimentalConditionService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class SystemController implements SystemControllerInterface {

	@Inject
	SystemSQLDAO systemSQLDAO;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	ConditionRelationService conditionRelationService;
	@Inject
	ExperimentalConditionService experimentalConditionService;
	
	@Override
	public void reindexEverything(Integer threadsToLoadObjects, Integer typesToIndexInParallel, Integer limitIndexedObjectsTo, Integer batchSizeToLoadObjects, Integer idFetchSize,
		Integer transactionTimeout) {
		systemSQLDAO.reindexEverything(threadsToLoadObjects, typesToIndexInParallel, limitIndexedObjectsTo, batchSizeToLoadObjects, idFetchSize, transactionTimeout);
	}

	@Override
	public ObjectResponse<Map<String, Object>> getSiteSummary() {
		return systemSQLDAO.getSiteSummary();
	}
	
	public void updateDiseaseAnnotationUniqueIds() {
		diseaseAnnotationService.updateUniqueIds();
	}

	@Override
	public void updateRefreshIntervalOnAllIndexes() {
		systemSQLDAO.setRefreshInterval();
	}

	@Override
	public void deleteUnusedConditionsAndExperiments() {
		List<Long> inUseCrIds = diseaseAnnotationService.getAllReferencedConditionRelationIds();
		conditionRelationService.deleteUnusedConditions(inUseCrIds);
		experimentalConditionService.deleteUnusedExperiments();
	}
}
