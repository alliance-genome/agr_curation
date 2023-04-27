package org.alliancegenome.curation_api.controllers;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.interfaces.DQMSubmissionInterface;
import org.alliancegenome.curation_api.jobs.BulkLoadManualProcessor;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

public class DQMSubmissionController implements DQMSubmissionInterface {

	@Inject
	BulkLoadManualProcessor bulkLoadManualProcessor;

	@Override
	public String update(MultipartFormDataInput input, Boolean cleanUp) {
		for (String key : input.getFormDataMap().keySet()) {
			String separator = "_";
			int sepPos = key.lastIndexOf(separator);
			BackendBulkLoadType loadType = BackendBulkLoadType.valueOf(key.substring(0, sepPos));
			BackendBulkDataProvider dataProvider = BackendBulkDataProvider.valueOf(key.substring(sepPos + 1));
			if (loadType == null || dataProvider == null) {
				return "FAIL";
			} else {
				bulkLoadManualProcessor.processBulkManualLoadFromDQM(input, loadType, dataProvider, cleanUp);
			}
		}

		return "OK";
	}

}
