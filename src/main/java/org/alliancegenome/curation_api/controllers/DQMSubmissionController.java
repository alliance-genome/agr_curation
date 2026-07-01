package org.alliancegenome.curation_api.controllers;

import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.interfaces.DQMSubmissionInterface;
import org.alliancegenome.curation_api.jobs.processors.BulkLoadManualProcessor;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.inject.Inject;

public class DQMSubmissionController implements DQMSubmissionInterface {

	@Inject
	BulkLoadManualProcessor bulkLoadManualProcessor;

	@Inject
	SpeciesService speciesService;

	@Override
	public String update(MultipartFormDataInput input, Boolean cleanUp) {
		for (String key : input.getFormDataMap().keySet()) {
			String separator = "_";
			int sepPos = key.lastIndexOf(separator);
			BackendBulkLoadType loadType = BackendBulkLoadType.valueOf(key.substring(0, sepPos));
			Species species = speciesService.getByDisplayName(key.substring(sepPos + 1));
			if (loadType == null || species == null) {
				return "FAIL";
			} else {
				bulkLoadManualProcessor.processBulkManualLoadFromDQM(input, loadType, species, cleanUp);
			}
		}

		return "OK";
	}

}
