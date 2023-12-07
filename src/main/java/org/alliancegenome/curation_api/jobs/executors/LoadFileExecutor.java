package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileExceptionDAO;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileHistoryDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.jobs.util.SlackNotifier;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.services.APIVersionInfoService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.base.BaseAssociationDTOCrudService;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.processing.LoadProcessDisplayService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;

public class LoadFileExecutor {

	@Inject
	protected ObjectMapper mapper;
	@Inject
	protected LoadProcessDisplayService loadProcessDisplayService;
	@Inject
	protected BulkLoadFileDAO bulkLoadFileDAO;
	@Inject
	BulkLoadFileHistoryDAO bulkLoadFileHistoryDAO;
	@Inject
	BulkLoadFileExceptionDAO bulkLoadFileExceptionDAO;
	@Inject
	APIVersionInfoService apiVersionInfoService;
	@Inject
	SlackNotifier slackNotifier;

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
			return null;
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
		if (bulkLoadFile.getLinkMLSchemaVersion() == null) {
			bulkLoadFile.setErrorMessage("Missing Schema Version");
			bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFile.getErrorMessage());
			bulkLoadFileDAO.merge(bulkLoadFile);
			return false;
		}
		if (!validSchemaVersion(bulkLoadFile.getLinkMLSchemaVersion(), dtoClass)) {
			bulkLoadFile.setErrorMessage("Invalid Schema Version: " + bulkLoadFile.getLinkMLSchemaVersion());
			bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
			slackNotifier.slackalert(bulkLoadFile.getErrorMessage());
			bulkLoadFileDAO.merge(bulkLoadFile);
			return false;
		}
		return true;
	}
	
	protected IngestDTO readIngestFile(BulkLoadFile bulkLoadFile, Class<?> dtoClass) {
		try {
			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (StringUtils.isNotBlank(ingestDto.getAllianceMemberReleaseVersion()))
				bulkLoadFile.setAllianceMemberReleaseVersion(ingestDto.getAllianceMemberReleaseVersion());
			
			if(!checkSchemaVersion(bulkLoadFile, dtoClass)) return null;

			return ingestDto;
		} catch (Exception e) {
			failLoad(bulkLoadFile, e);
			e.printStackTrace();
		}
		return null;
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
	public void runCleanup(DiseaseAnnotationService service, BulkLoadFileHistory history, String dataProviderName, List<Long> annotationIdsBefore, List<Long> annotationIdsAfter, String md5sum) {
		Log.debug("runLoad: After: " + dataProviderName + " " + annotationIdsAfter.size());

		List<Long> distinctAfter = annotationIdsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(annotationIdsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProviderName + " " + idsToRemove.size());

		history.setTotalDeleteRecords((long)idsToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of disease annotations linked to unloaded " + dataProviderName, idsToRemove.size());
		for (Long id : idsToRemove) {
			try {
				String loadDescription = dataProviderName + " disease annotation bulk load (" + md5sum + ")";
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
	
	protected <S extends BaseDTOCrudService<?, ?, ?>> void runCleanup(S service, BulkLoadFileHistory history, BulkLoadFile bulkLoadFile, List<String> curiesBefore, List<String> curiesAfter) {
		BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
		String dataProviderName = manual.getDataProvider().name();
		Log.debug("runLoad: After: " + dataProviderName + " " + curiesAfter.size());

		List<String> distinctAfter = curiesAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<String> curiesToRemove = ListUtils.subtract(curiesBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProviderName + " " + curiesToRemove.size());

		history.setTotalDeleteRecords((long)curiesToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of primary objects " + dataProviderName, curiesToRemove.size());
		for (String curie : curiesToRemove) {
			try {
				String loadDescription = dataProviderName + " " + manual.getBackendBulkLoadType() + " bulk load (" + bulkLoadFile.getMd5Sum() + ")";
				service.removeOrDeprecateNonUpdated(curie, loadDescription);
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{ \"curie\": \"" + curie + "\"}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
	}
	
	protected <S extends BaseAssociationDTOCrudService<?, ?, ?>> void runCleanup(S service, BulkLoadFileHistory history, String dataProviderName, List<Long> idsBefore, List<Long> idsAfter, String md5sum) {
		Log.debug("runLoad: After: " + dataProviderName + " " + idsAfter.size());

		List<Long> distinctAfter = idsAfter.stream().distinct().collect(Collectors.toList());
		Log.debug("runLoad: Distinct: " + dataProviderName + " " + distinctAfter.size());

		List<Long> idsToRemove = ListUtils.subtract(idsBefore, distinctAfter);
		Log.debug("runLoad: Remove: " + dataProviderName + " " + idsToRemove.size());

		history.setTotalDeleteRecords((long)idsToRemove.size());
		
		ProcessDisplayHelper ph = new ProcessDisplayHelper(1000);
		ph.startProcess("Deletion/deprecation of associations " + dataProviderName, idsToRemove.size());
		for (Long id : idsToRemove) {
			try {
				String loadDescription = dataProviderName + " association bulk load (" + md5sum + ")";
				service.deprecateOrDeleteAssociation(id, false, loadDescription, false);
				history.incrementDeleted();
			} catch (Exception e) {
				history.incrementDeleteFailed();
				addException(history, new ObjectUpdateExceptionData("{ \"id\": \"" + id + "\"}", e.getMessage(), e.getStackTrace()));
			}
			ph.progressProcess();
		}
		ph.finishProcess();
		
	}
	
	protected void failLoad(BulkLoadFile bulkLoadFile, Exception e) {
		Set<String> errorMessages = new LinkedHashSet<String>();
		errorMessages.add(e.getMessage());
		errorMessages.add(e.getLocalizedMessage());
		Throwable cause = e.getCause();
		while (e.getCause() != null) {
			errorMessages.add(cause.getMessage());
			cause = cause.getCause();
		}
		bulkLoadFile.setErrorMessage(String.join("|", errorMessages));
		bulkLoadFile.setBulkloadStatus(JobStatus.FAILED);
		slackNotifier.slackalert(bulkLoadFile.getErrorMessage());
		bulkLoadFileDAO.merge(bulkLoadFile);
	}
}
