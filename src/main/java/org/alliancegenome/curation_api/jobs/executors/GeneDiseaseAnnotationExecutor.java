package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.services.GeneDiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	GeneDiseaseAnnotationService geneDiseaseAnnotationService;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getSpeciesName());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, GeneDiseaseAnnotationDTO.class))
				return;
			List<GeneDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseGeneIngestSet();
			String speciesName = manual.getDataType().getSpeciesName();

			if (annotations == null)
				annotations = new ArrayList<>();
			
			bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());		
			bulkLoadFileDAO.merge(bulkLoadFile);
			trackHistory(runLoad(speciesName, annotations), bulkLoadFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Gets called from the API directly
	public APIResponse runLoad(String speciesName, List<GeneDiseaseAnnotationDTO> annotations) {

		List<Long> annotationIdsBefore = new ArrayList<>();
		annotationIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(speciesName));
		annotationIdsBefore.removeIf(Objects::isNull);

		log.debug("runLoad: Before: " + speciesName + " " + annotationIdsBefore.size());
		List<Long> annotationIdsAfter = new ArrayList<>();
		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());
		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Gene Disease Annotation Update " + speciesName, annotations.size());
		annotations.forEach(annotationDTO -> {

			try {
				GeneDiseaseAnnotation annotation = geneDiseaseAnnotationService.upsert(annotationDTO);
				history.incrementCompleted();
				annotationIdsAfter.add(annotation.getId());
			} catch (ObjectUpdateException e) {
				addException(history, e.getData());
			} catch (Exception e) {
				addException(history, new ObjectUpdateExceptionData(annotationDTO, e.getMessage(), e.getStackTrace()));
			}

			ph.progressProcess();
		});
		ph.finishProcess();

		diseaseAnnotationService.removeNonUpdatedAnnotations(speciesName, annotationIdsBefore, annotationIdsAfter);

		return new LoadHistoryResponce(history);
	}

}
