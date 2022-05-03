package org.alliancegenome.curation_api.jobs.executors;

import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.response.*;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class GeneFmsExecutor extends LoadFileExecutor {

    @Inject GeneDAO geneDAO;

    @Inject GeneService geneService;

    
    public void runLoad(BulkLoadFile bulkLoadFile) {

        try {
            GeneMetaDataFmsDTO geneData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(geneData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);

            trackHistory(runLoad(geneData), bulkLoadFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gets called from the API directly
    public APIResponse runLoad(GeneMetaDataFmsDTO geneData) {
        List<String> taxonIDs = geneData.getData().stream()
                                 .map( geneDTO -> geneDTO.getBasicGeneticEntity().getTaxonId() ).distinct().collect( Collectors.toList() );

        List<String> annotationsIdsBefore = new ArrayList<String>();
        for(String taxonID: taxonIDs) {
            List<String> annotationIds = geneDAO.findAllAnnotationIds(taxonID);
            log.debug("runLoad: Before: taxonID " + taxonID + " " + annotationIds.size());
            annotationsIdsBefore.addAll(annotationIds);
        }

        log.debug("runLoad: Before: total " + annotationsIdsBefore.size());
        List<String> annotationsIdsLoaded = new ArrayList<>();
        
        BulkLoadFileHistory history = new BulkLoadFileHistory(geneData.getData().size());
        
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);

        ph.startProcess("Gene FMS DTO Update", geneData.getData().size());

        for(GeneFmsDTO geneDTO: geneData.getData()) {
            
            try {
                Gene annotation = geneService.processUpdate(geneDTO);
                history.incrementCompleted();
                annotationsIdsLoaded.add(annotation.getCurie());
            } catch (ObjectUpdateException e) {
                addException(history, e.getData());
            } catch (Exception e) {
                addException(history, new ObjectUpdateExceptionData(geneDTO, e.getMessage()));
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
            SearchResponse<Gene> gene = geneDAO.findByField("curie", curie);
            if (gene != null && gene.getTotalResults() == 1) {
                geneDAO.remove(gene.getResults().get(0).getCurie());
            } else {
                log.error("Failed getting annotation: " + curie);
            }
        }

        return new LoadHistoryResponce(history);
    }
}
