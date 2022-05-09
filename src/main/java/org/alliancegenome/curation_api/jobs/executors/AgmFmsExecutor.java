package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AgmFmsExecutor extends LoadFileExecutor {

    @Inject AffectedGenomicModelDAO affectedGenomicModelDAO;

    @Inject AffectedGenomicModelService affectedGenomicModelService;

    public void runLoad(BulkLoadFile bulkLoadFile) {
        try {
            AffectedGenomicModelMetaDataFmsDTO agmData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModelMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(agmData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            trackHistory(runLoad(agmData), bulkLoadFile);
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
    // Gets called from the API directly
    public APIResponse runLoad(AffectedGenomicModelMetaDataFmsDTO agmData) {
        List<String> taxonIDs = agmData.getData().stream()
                                 .map( agmDTO -> agmDTO.getTaxonId() ).distinct().collect( Collectors.toList() );

        List<String> annotationsIdsBefore = new ArrayList<String>();
        for(String taxonID: taxonIDs) {
            List<String> annotationIds = affectedGenomicModelDAO.findAllCuriesByTaxon(taxonID);
            log.debug("runLoad: Before: taxonID " + taxonID + " " + annotationIds.size());
            annotationsIdsBefore.addAll(annotationIds);
        }

        log.debug("runLoad: Before: total " + annotationsIdsBefore.size());
        List<String> annotationsIdsLoaded = new ArrayList<>();
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(agmData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("AGM FMS DTO Update", agmData.getData().size());
        for(AffectedGenomicModelFmsDTO agmDTO: agmData.getData()) {
            try {
                AffectedGenomicModel annotation = affectedGenomicModelService.processUpdate(agmDTO);
                history.incrementCompleted();
                annotationsIdsLoaded.add(annotation.getCurie());
            } catch (ObjectUpdateException e) {
                addException(history, e.getData());
            } catch (Exception e) {
                addException(history, new ObjectUpdateExceptionData(agmDTO, e.getMessage()));
            }
            
            ph.progressProcess();
        }
        ph.finishProcess();

        log.debug("runLoad: Loaded: " + taxonIDs.toString() + " " + annotationsIdsLoaded.size());

        List<String> distinctLoaded = annotationsIdsLoaded.stream().distinct().collect(Collectors.toList());
        log.debug("runLoad: Distinct loaded: " + taxonIDs.toString() + " " + distinctLoaded.size());

        List<String> curiesToRemove = ListUtils.subtract(annotationsIdsBefore, distinctLoaded);
        log.debug("runLoad: Remove: " + taxonIDs.toString() + " " + curiesToRemove.size());

        for (String curie : curiesToRemove) {
            SearchResponse<AffectedGenomicModel> agm = affectedGenomicModelDAO.findByField("curie", curie);
            if (agm != null && agm.getTotalResults() == 1) {
                affectedGenomicModelDAO.remove(agm.getResults().get(0).getCurie());
            } else {
                log.error("Failed getting annotation: " + curie);
            }
        }

        return new LoadHistoryResponce(history);
    }
}
