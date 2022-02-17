package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.DiseaseAnnotationMetaDataFmsDTO;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.DiseaseAnnotationFmsService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class DiseaseAnnotationFmsExecutor extends LoadFileExecutor {

    @Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    @Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
    @Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
    @Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
    
    @Inject DiseaseAnnotationFmsService diseaseAnnotationFmsService;

    public void runLoad(BulkLoadFile bulkLoadFile) {
        try {
            DiseaseAnnotationMetaDataFmsDTO diseaseData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), DiseaseAnnotationMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(diseaseData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            BulkFMSLoad fms = (BulkFMSLoad)bulkLoadFile.getBulkLoad();
            String taxonId = BackendBulkDataType.valueOf(fms.getDataSubType()).getTaxonId();
            
            trackHistory(runLoad(taxonId, diseaseData), bulkLoadFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public APIResponse runLoad(String taxonID, DiseaseAnnotationMetaDataFmsDTO annotationData) {
        List<String> annotationsIdsBefore = new ArrayList<String>();
        annotationsIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        annotationsIdsBefore.addAll(alleleDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));
        annotationsIdsBefore.addAll(agmDiseaseAnnotationDAO.findAllAnnotationIds(taxonID));

        log.debug("runLoad: Before: " + taxonID + " " + annotationsIdsBefore.size());
        List<String> annotationsIdsAfter = new ArrayList<>();
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(annotationData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonID, annotationData.getData().size());
        annotationData.getData().forEach(annotationDTO -> {
            try {
                DiseaseAnnotation annotation = diseaseAnnotationFmsService.upsert(annotationDTO);
                history.incrementCompleted();
                annotationsIdsAfter.add(annotation.getUniqueId());
            } catch (ObjectUpdateException e) {
                history.getExceptions().add(e.getData());
                history.incrementFailed();
            } catch (Exception e) {
                e.printStackTrace();
                history.getExceptions().add(new ObjectUpdateExceptionData(annotationDTO, e.getMessage()));
                history.incrementFailed();
            }
            ph.progressProcess();
        });
        ph.finishProcess();

        log.debug("runLoad: After: " + taxonID + " " + annotationsIdsAfter.size());

        List<String> distinctAfter = annotationsIdsAfter.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct: " + taxonID + " " + distinctAfter.size());

        List<String> curiesToRemove = ListUtils.subtract(annotationsIdsBefore, distinctAfter);
        log.debug("runLoad: Remove: " + taxonID + " " + curiesToRemove.size());

        for (String curie : curiesToRemove) {
            SearchResponse<DiseaseAnnotation> da = diseaseAnnotationDAO.findByField("uniqueId", curie);
            if (da != null && da.getTotalResults() == 1) {
                diseaseAnnotationDAO.remove(da.getResults().get(0).getId());
            } else {
                log.error("Failed getting annotation: " + curie);
            }
        }
        return new LoadHistoryResponce(history);
    }
    
    
    
}
