package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.response.APIResponse;
import org.alliancegenome.curation_api.response.LoadHistoryResponce;
import org.alliancegenome.curation_api.services.AlleleDiseaseAnnotationService;
import org.alliancegenome.curation_api.services.DiseaseAnnotationService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleDiseaseAnnotationExecutor extends LoadFileExecutor {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationService alleleDiseaseService;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	public void runLoad(BulkLoadFile bulkLoadFile) {

		try {
			BulkManualLoad manual = (BulkManualLoad) bulkLoadFile.getBulkLoad();
			log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getSpeciesName());

			IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
			bulkLoadFile.setLinkMLSchemaVersion(getVersionNumber(ingestDto.getLinkMLVersion()));
			if (!validateSchemaVersion(bulkLoadFile, AlleleDiseaseAnnotationDTO.class))
				return;
			List<AlleleDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAlleleIngestSet();
			String speciesName = manual.getDataType().getSpeciesName();

			if (annotations != null) {
				bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());

				bulkLoadFileDAO.merge(bulkLoadFile);

				trackHistory(runLoad(speciesName, annotations), bulkLoadFile);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Gets called from the API directly
	public APIResponse runLoad(String speciesName, List<AlleleDiseaseAnnotationDTO> annotations) {

		List<Long> annotationIdsBefore = new ArrayList<>();
		annotationIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(speciesName));
		annotationIdsBefore.removeIf(Objects::isNull);

		log.debug("runLoad: Before: " + speciesName + " " + annotationIdsBefore.size());
		List<Long> annotationIdsAfter = new ArrayList<>();

		BulkLoadFileHistory history = new BulkLoadFileHistory(annotations.size());

		ProcessDisplayHelper ph = new ProcessDisplayHelper(2000);
		ph.addDisplayHandler(processDisplayService);
		ph.startProcess("Allele Disease Annotation Update " + speciesName, annotations.size());
		annotations.forEach(annotationDTO -> {
			try {
				AlleleDiseaseAnnotation annotation = alleleDiseaseService.upsert(annotationDTO, null);
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
