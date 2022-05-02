package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class AlleleFmsExecutor extends LoadFileExecutor {

    @Inject AlleleDAO alleleDAO;

    @Inject AlleleService alleleService;
    
    public void runLoad(BulkLoadFile bulkLoadFile) {

        try {
            AlleleMetaDataFmsDTO alleleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AlleleMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(alleleData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            trackHistory(runLoad(alleleData), bulkLoadFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Gets called from the API directly
    public APIResponse runLoad(AlleleMetaDataFmsDTO alleleData) {
        List<String> taxonIDs = alleleData.getData().stream()
                                 .map( alleleDTO -> alleleDTO.getTaxonId() ).distinct().collect( Collectors.toList() );

        List<String> annotationsIdsBefore = new ArrayList<String>();
        for(String taxonID: taxonIDs) {
            List<String> annotationIds = alleleDAO.findAllAnnotationIds(taxonID);
            log.debug("runLoad: Before: taxonID " + taxonID + " " + annotationIds.size());
            annotationsIdsBefore.addAll(annotationIds);
        }

        log.debug("runLoad: Before: total " + annotationsIdsBefore.size());
        List<String> annotationsIdsLoaded = new ArrayList<>();
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(alleleData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Allele FMS DTO Update", alleleData.getData().size());

        for(AlleleFmsDTO alleleDTO: alleleData.getData()) {
            try {
                Allele annotation = alleleService.processUpdate(alleleDTO);
                history.incrementCompleted();
                annotationsIdsLoaded.add(annotation.getCurie());
            } catch (ObjectUpdateException e) {
                addException(history, e.getData());
            } catch (Exception e) {
                addException(history, new ObjectUpdateExceptionData(alleleDTO, e.getMessage()));
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
            SearchResponse<Allele> allele = alleleDAO.findByField("curie", curie);
            if (allele != null && allele.getTotalResults() == 1) {
                alleleDAO.remove(allele.getResults().get(0).getCurie());
            } else {
                log.error("Failed getting annotation: " + curie);
            }
        }
        return new LoadHistoryResponce(history);
    }


}
