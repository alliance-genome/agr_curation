package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.ExperimentalConditionDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.ExperimentalConditionValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ExperimentalConditionService extends BaseEntityCrudService<ExperimentalCondition, ExperimentalConditionDAO> {

	@Inject ExperimentalConditionDAO experimentalConditionDAO;
	@Inject ExperimentalConditionValidator experimentalConditionValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(experimentalConditionDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<ExperimentalCondition> update(ExperimentalCondition uiEntity) {
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionUpdate(uiEntity);
		return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<ExperimentalCondition> create(ExperimentalCondition uiEntity) {
		ExperimentalCondition dbEntity = experimentalConditionValidator.validateExperimentalConditionCreate(uiEntity);
		return new ObjectResponse<>(experimentalConditionDAO.persist(dbEntity));
	}

	public void deleteUnusedExperiments() {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		List<Long> experimentIds = experimentalConditionDAO.findAllIds().getResults();
		pdh.startProcess("Delete unused Experiments", experimentIds.size());
		experimentIds.forEach(ecId -> {
			try {
				experimentalConditionDAO.remove(ecId);
			} catch (ApiErrorException ex) {
				Log.debug("Skipping deletion of experiment " + ecId + " as still in use");
			}
			pdh.progressProcess();
		});
		pdh.finishProcess(null);
	}

}
