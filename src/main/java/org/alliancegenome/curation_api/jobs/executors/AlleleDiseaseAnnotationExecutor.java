package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleDiseaseAnnotationExecutor extends LoadFileExecutor {

    @Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject AlleleDiseaseAnnotationService alleleDiseaseService;
    @Inject DiseaseAnnotationService diseaseAnnotationService;

    public void runLoad(BulkLoadFile bulkLoadFile) {

        try {
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());

            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            List<AlleleDiseaseAnnotationDTO> annotations = ingestDto.getDiseaseAlleleIngestSet();
            String taxonId = manual.getDataType().getTaxonId();

            if (annotations != null) {
                bulkLoadFile.setRecordCount(annotations.size() + bulkLoadFile.getRecordCount());
                bulkLoadFileDAO.merge(bulkLoadFile);
                runLoad(taxonId, annotations);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gets called from the API directly
    public APIResponse runLoad(String taxonId, List<AlleleDiseaseAnnotationDTO> annotations) {

        List<String> annotationIdsBefore = new ArrayList<>();
        annotationIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(taxonId));
        annotationIdsBefore.removeIf(Objects::isNull);

        log.debug("runLoad: Before: " + taxonId + " " + annotationIdsBefore.size());
        List<String> annotationIdsAfter = new ArrayList<>();
        
        BulkLoadHistory history = new BulkLoadHistory(annotations.size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele Disease Annotation Update " + taxonId, annotations.size());
        annotations.forEach(annotationDTO -> {
            try {
                AlleleDiseaseAnnotation annotation = alleleDiseaseService.upsert(annotationDTO);
                history.incrementCompleted();
                annotationIdsAfter.add(annotation.getUniqueId());
            } catch (ObjectUpdateException e) {
                history.getExceptions().add(e);
                history.incrementFailed();
            } catch (Exception e) {
                history.getExceptions().add(new ObjectUpdateException(annotationDTO, e.getMessage()));
                history.incrementFailed();
            }

            ph.progressProcess();
        });
        ph.finishProcess();

        diseaseAnnotationService.removeNonUpdatedAnnotations(taxonId, annotationIdsBefore, annotationIdsAfter);
        return new LoadHistoryResponce(history);
    }
}
