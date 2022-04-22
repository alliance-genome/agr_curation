package org.alliancegenome.curation_api.jobs;

import static org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType.*;

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.jobs.executors.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.MoleculeService;
import org.alliancegenome.curation_api.services.helpers.*;
import org.alliancegenome.curation_api.services.ontology.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadJobExecutor {

    @Inject ObjectMapper mapper;


    @Inject XcoTermService xcoTermService;
    @Inject GoTermService goTermService;
    @Inject SoTermService soTermService;
    @Inject EcoTermService ecoTermService;
    @Inject ZecoTermService zecoTermService;
    @Inject EmapaTermService emapaTermService;
    @Inject DaoTermService daoTermService;
    @Inject CHEBITermService chebiTermService;
    @Inject ZfaTermService zfaTermService;
    @Inject DoTermService doTermService;
    @Inject WbbtTermService wbbtTermService;
    @Inject MpTermService mpTermService;
    @Inject MaTermService maTermService;
    @Inject WblsTermService wblsTermService;
    @Inject FbdvTermService fbdvTermService;
    @Inject MmusdvTermService mmusdvTermService;
    @Inject ZfsTermService zfsTermService;
    @Inject XbaTermService xbaTermService;
    @Inject XbsTermService xbsTermService;
    @Inject XpoTermService xpoTermService;
    @Inject XbedTermService xbedTermService;
    @Inject XsmoTermService xsmoTermService;

    @Inject MoleculeService moleculeService;

    @Inject BulkLoadFileDAO bulkLoadFileDAO;

    @Inject AlleleDiseaseAnnotationExecutor alleleDiseaseAnnotationExecutor;
    @Inject AgmDiseaseAnnotationExecutor agmDiseaseAnnotationExecutor;
    @Inject GeneDiseaseAnnotationExecutor geneDiseaseAnnotationExecutor;
    @Inject GeneFmsExecutor geneFmsExecutor;
    @Inject AlleleFmsExecutor alleleFmsExecutor;
    @Inject AgmFmsExecutor agmFmsExecutor;
    @Inject DiseaseAnnotationFmsExecutor diseaseAnnotationFmsExecutor;
    @Inject MoleculeExecutor moleculeExecutor;


    public void process(BulkLoadFile bulkLoadFile) throws Exception {

        BackendBulkLoadType loadType = bulkLoadFile.getBulkLoad().getBackendBulkLoadType();
        
        List<BackendBulkLoadType> ingestTypes = List.of(
            AGM_DISEASE_ANNOTATION,
            ALLELE_DISEASE_ANNOTATION,
            GENE_DISEASE_ANNOTATION,
            DISEASE_ANNOTATION,
            FULL_INGEST
        );

        if(ingestTypes.contains(loadType)) {

            bulkLoadFile.setRecordCount(0);
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            if(loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                alleleDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
            }
            if(loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                agmDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
            }
            if(loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                geneDiseaseAnnotationExecutor.runLoad(bulkLoadFile);
            }

        } else if(loadType == BackendBulkLoadType.GENE_DTO) {
            geneFmsExecutor.runLoad(bulkLoadFile);
        } else if(loadType == BackendBulkLoadType.ALLELE_DTO) {
            alleleFmsExecutor.runLoad(bulkLoadFile);
        } else if(loadType == BackendBulkLoadType.AGM_DTO) {
            agmFmsExecutor.runLoad(bulkLoadFile);
        } else if(loadType == BackendBulkLoadType.DISEASE_ANNOTATION_DTO) {
            diseaseAnnotationFmsExecutor.runLoad(bulkLoadFile);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
            moleculeExecutor.runLoad(bulkLoadFile);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
            bulkLoadFile.setRecordCount(0);
            GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
            BaseOntologyTermService service = null;
            OntologyBulkLoadType ontologyType = bulkLoadFile.getBulkLoad().getOntologyType();
            
            if(ontologyType == OntologyBulkLoadType.ZECO) {
                config.setLoadOnlyIRIPrefix("ZECO");
                service = zecoTermService;
                processTerms(bulkLoadFile, zecoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.EMAPA) {
                config.getAltNameSpaces().add("anatomical_structure");
                service = emapaTermService;
                processTerms(bulkLoadFile, emapaTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.GO) {
                config.getAltNameSpaces().add("biological_process");
                config.getAltNameSpaces().add("molecular_function");
                config.getAltNameSpaces().add("cellular_component");
                service = goTermService;
                processTerms(bulkLoadFile, goTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.SO) {
                processTerms(bulkLoadFile, soTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XCO) {
                processTerms(bulkLoadFile, xcoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ECO) {
                processTerms(bulkLoadFile, ecoTermService, config);
                ecoTermService.updateAbbreviations();
            } else if(ontologyType == OntologyBulkLoadType.CHEBI) {
                processTerms(bulkLoadFile, chebiTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ZFA) {
                config.getAltNameSpaces().add("zebrafish_anatomy");
                processTerms(bulkLoadFile, zfaTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.DO) {
                processTerms(bulkLoadFile, doTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MP) {
                config.setLoadOnlyIRIPrefix("MP");
                processTerms(bulkLoadFile, mpTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MA) {
                processTerms(bulkLoadFile, maTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.WBBT) {
                processTerms(bulkLoadFile, wbbtTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.DAO) {
                config.setLoadOnlyIRIPrefix("FBbt");
                processTerms(bulkLoadFile,  daoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.WBLS) {
                processTerms(bulkLoadFile, wblsTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.FBDV) {
                processTerms(bulkLoadFile, fbdvTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MMUSDV) {
                config.getAltNameSpaces().add("mouse_developmental_stage");
                config.getAltNameSpaces().add("mouse_stages_ontology");
                processTerms(bulkLoadFile, mmusdvTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ZFS) {
                processTerms(bulkLoadFile, zfsTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XBA_XBS) {
                processTerms(bulkLoadFile, OntologyBulkLoadType.XBA, xbaTermService, config);
                GenericOntologyLoadConfig config2 = new GenericOntologyLoadConfig();
                config2.getAltNameSpaces().add("xenopus_developmental_stage");
                processTerms(bulkLoadFile, OntologyBulkLoadType.XBS, xbsTermService, config2);
            } else if(ontologyType == OntologyBulkLoadType.XPO) {
                config.setLoadOnlyIRIPrefix("XPO");
                processTerms(bulkLoadFile,xpoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XBED) {
                processTerms(bulkLoadFile, xbedTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XSMO) {
                processTerms(bulkLoadFile, xsmoTermService, config);
            } else {
                log.info("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
                throw new Exception("Ontolgy Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
            }
        } else {
            log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
            throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
        }
        log.info("Process Finished for: " + bulkLoadFile.getBulkLoad().getName());
    }
    
    private void processTerms(BulkLoadFile bulkLoadFile, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
        processTerms(bulkLoadFile, bulkLoadFile.getBulkLoad().getOntologyType(), service, config);
    }
    
    private void processTerms(BulkLoadFile bulkLoadFile, OntologyBulkLoadType ontologyType, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {

        GenericOntologyLoadHelper loader;
        if(config != null) {
            loader = new GenericOntologyLoadHelper(ontologyType.getClazz(), config);
        } else {
            loader = new GenericOntologyLoadHelper(ontologyType.getClazz());
        }

        Map<String, ? extends OntologyTerm> termMap = loader.load(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));

        bulkLoadFile.setRecordCount(bulkLoadFile.getRecordCount() + termMap.size());
        bulkLoadFileDAO.merge(bulkLoadFile);
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + ontologyType.getClazz().getSimpleName() + " Database Persistance", termMap.size());
        for(String termKey: termMap.keySet()) {
            service.processUpdate(termMap.get(termKey));
            ph.progressProcess();
        }
        ph.finishProcess();
    }


}
