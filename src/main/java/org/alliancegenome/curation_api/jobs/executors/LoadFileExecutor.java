package org.alliancegenome.curation_api.jobs.executors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.services.APIVersionInfoService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.ProcessDisplayService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;

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

	protected void trackHistory(BulkLoadFileHistory history, BulkLoadFile bulkLoadFile) {
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
	}

	protected String getVersionNumber(String versionString) {
		if (StringUtils.isBlank(versionString))
			return LinkMLSchemaConstants.LATEST_RELEASE;
		if (versionString.startsWith("v"))
			return versionString.substring(1);
		return versionString;
	}
	
	private List<Integer> getVersionParts(String version) {
		List<String> stringParts = new ArrayList<String>(Arrays.asList(version.split("\\.")));
		List<Integer> intParts = new ArrayList<Integer>();
		for (String part : stringParts) {
			try {
				Integer intPart = Integer.parseInt(part);
				intParts.add(intPart);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		
		while (intParts.size() < 3) { intParts.add(0); }
		
		return intParts;
	}
	

	protected boolean checkSchemaVersion(BulkLoadFile bulkLoadFile, Class<?> dtoClass) {
		if (!validSchemaVersion(bulkLoadFile.getLinkMLSchemaVersion(), dtoClass)) {
			bulkLoadFile.setErrorMessage("Invalid Schema Version: " + bulkLoadFile.getLinkMLSchemaVersion());
			bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
			bulkLoadFileDAO.merge(bulkLoadFile);
			return false;
		}
		return true;
	}
	
	protected boolean validSchemaVersion(String submittedSchemaVersion, Class<?> dtoClass) {
		
		List<String> versionRange = apiVersionInfoService.getVersionRange(dtoClass.getAnnotation(AGRCurationSchemaVersion.class));
		List<Integer> minVersionParts = getVersionParts(versionRange.get(0));
		List<Integer> maxVersionParts = getVersionParts(versionRange.get(1));
		List<Integer> fileVersionParts = getVersionParts(submittedSchemaVersion);
		
		if (minVersionParts == null || maxVersionParts == null || fileVersionParts == null)
			return false;
		
		// check not lower than min version
		if (fileVersionParts.get(0) < minVersionParts.get(0)) return false;
		if (fileVersionParts.get(0).equals(minVersionParts.get(0))) {
			if (fileVersionParts.get(1) < minVersionParts.get(1)) return false;
			if (fileVersionParts.get(1).equals(minVersionParts.get(1))) {
				if (fileVersionParts.get(2) < minVersionParts.get(2)) return false;
			}
		}
		// check not higher than max version
		if (fileVersionParts.get(0) > maxVersionParts.get(0)) return false;
		if (fileVersionParts.get(0).equals(maxVersionParts.get(0))) {
			if (fileVersionParts.get(1) > maxVersionParts.get(1)) return false;
			if (fileVersionParts.get(1).equals(maxVersionParts.get(1))) {
				if (fileVersionParts.get(2) > maxVersionParts.get(2)) return false;
			}
		}
		
		return true;
	}
	

	// The following methods are for bulk validation
	public void runCleanup(DiseaseAnnotationService service, BulkLoadFileHistory history, String dataProvider, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter) {
		Log.debug("runLoad: After: " + dataProvider + " " + annotationIdsAfter.size());

		List<Long> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProvider + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProvider + " " + idsToRemove.size());

		history.setTotalDeleteRecords((long)idsToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + dataProvider, idsToRemove.size());
		for (Long id : idsToRemove) {
			try {
				String loadDescription = dataProvider + " disease annotation bulk load (" + history.getBulkLoadFile().getMd5Sum() + ")";
				service.deprecateOrDeleteAnnotationAndNotes(id, false, loadDescription, true);
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{ \"id\": " + id + "}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
	}
	
	protected <S extends BaseDTOCrudService<?, ?, ?>> void runCleanup(S service, BulkLoadFileHistory history, String dataProvider, List<String> curiesBefore, List<String> curiesAfter) {
		Log.debug("runLoad: After: " + dataProvider + " " + curiesAfter.size());

		List<String> distinctAfter = curiesAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProvider + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(curiesBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProvider + " " + curiesToRemove.size());

		history.setTotalDeleteRecords((long)curiesToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of primary objects " + dataProvider, curiesToRemove.size());
		for (String curie : curiesToRemove) {
			try {
				service.removeOrDeprecateNonUpdated(curie, dataProvider, history.getBulkLoadFile().getMd5Sum());
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{ \"curie\": \"" + curie + "\"}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
	}
}
