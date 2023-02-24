package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.APIVersionInfoService;
import org.alliancegenome.curation_api.services.ProcessDisplayService;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoadFileExecutor {

	@Inject
	ObjectMapper mapper;
	@Inject
	ProcessDisplayService processDisplayService;
	@Inject
	BulkLoadFileDAO bulkLoadFileDAO;
	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject
	BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	@Inject
	APIVersionInfoService apiVersionInfoService;

	protected void trackHistory(APIResponse runHistory, BulkLoadFile bulkLoadFile) {
		LoadHistoryResponce res = (LoadHistoryResponce) runHistory;
		BulkLoadFileHistory history = res.getHistory();

		history.setBulkLoadFile(bulkLoadFile);
		bulkLoadFileHistoryDAO.persist(history);

		for (BulkLoadFileException e : history.getExceptions()) {
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

	protected String getVersionNumber(String versionString) {
		if (StringUtils.isBlank(versionString))
			return LinkMLSchemaConstants.LATEST_RELEASE;
		if (versionString.startsWith("v"))
			return versionString.substring(1);
		return versionString;
	}
	
	protected boolean validSchemaVersion(String submittedSchemaVersion, Class<?> dtoClass) {
		
		List<String> versionRange = apiVersionInfoService.getVersionRange(dtoClass.getAnnotation(AGRCurationSchemaVersion.class));
		List<String> minVersionParts = new ArrayList<String>(Arrays.asList(versionRange.get(0).split("\\.")));
		List<String> maxVersionParts = new ArrayList<String>(Arrays.asList(versionRange.get(1).split("\\.")));
		List<String> fileVersionParts = new ArrayList<String>(Arrays.asList(submittedSchemaVersion.split("\\.")));
		
		while (fileVersionParts.size() < 3) { fileVersionParts.add("0"); }
		
		// check not lower than min version
		if (Integer.parseInt(fileVersionParts.get(0)) < Integer.parseInt(minVersionParts.get(0))) return false;
		if (Integer.parseInt(fileVersionParts.get(0)) == Integer.parseInt(minVersionParts.get(0))) {
			if (Integer.parseInt(fileVersionParts.get(1)) < Integer.parseInt(minVersionParts.get(1))) return false;
			if (Integer.parseInt(fileVersionParts.get(1)) == Integer.parseInt(minVersionParts.get(1))) {
				if (Integer.parseInt(fileVersionParts.get(2)) < Integer.parseInt(minVersionParts.get(2))) return false;
			}
		}
		// check not higher than max version
		if (Integer.parseInt(fileVersionParts.get(0)) > Integer.parseInt(maxVersionParts.get(0))) return false;
		if (Integer.parseInt(fileVersionParts.get(0)) == Integer.parseInt(maxVersionParts.get(0))) {
			if (Integer.parseInt(fileVersionParts.get(1)) > Integer.parseInt(maxVersionParts.get(1))) return false;
			if (Integer.parseInt(fileVersionParts.get(1)) == Integer.parseInt(maxVersionParts.get(1))) {
				if (Integer.parseInt(fileVersionParts.get(2)) > Integer.parseInt(maxVersionParts.get(2))) return false;
			}
		}
		
		return true;
	}

}
