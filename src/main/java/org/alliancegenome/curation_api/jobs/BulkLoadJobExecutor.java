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


    public <T extends OntologyTerm> void process(BulkLoadFile bulkLoadFile) throws Exception {

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
            GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
            BaseOntologyTermService service = null;
            OntologyBulkLoadType ontologyType = bulkLoadFile.getBulkLoad().getOntologyType();
            
            if(ontologyType == OntologyBulkLoadType.ZECO) {
                config.setLoadOnlyIRIPrefix("ZECO");
                service = zecoTermService;
                processTerms(bulkLoadFile, ontologyType.getClazz(), zecoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.EMAPA) {
                config.getAltNameSpaces().add("anatomical_structure");
                service = emapaTermService;
                processTerms(bulkLoadFile, ontologyType.getClazz(), emapaTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.GO) {
                config.getAltNameSpaces().add("biological_process");
                config.getAltNameSpaces().add("molecular_function");
                config.getAltNameSpaces().add("cellular_component");
                service = goTermService;
                processTerms(bulkLoadFile, ontologyType.getClazz(), goTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.SO) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), soTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XCO) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), xcoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ECO) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), ecoTermService, config);
                ecoTermService.updateAbbreviations();
            } else if(ontologyType == OntologyBulkLoadType.CHEBI) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), chebiTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ZFA) {
                config.getAltNameSpaces().add("zebrafish_anatomy");
                processTerms(bulkLoadFile, ontologyType.getClazz(), zfaTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.DO) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), doTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MP) {
                config.setLoadOnlyIRIPrefix("MP");
                processTerms(bulkLoadFile, ontologyType.getClazz(), mpTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MA) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), maTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.WBBT) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), wbbtTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.DAO) {
                config.setLoadOnlyIRIPrefix("FBbt");
                processTerms(bulkLoadFile, ontologyType.getClazz(), daoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.WBLS) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), wblsTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.FBDV) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), fbdvTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.MMUSDV) {
                config.getAltNameSpaces().add("mouse_developmental_stage");
                config.getAltNameSpaces().add("mouse_stages_ontology");
                processTerms(bulkLoadFile, ontologyType.getClazz(), mmusdvTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.ZFS) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), zfsTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XBA_XBS) {
                GenericOntologyLoadConfig config2 = new GenericOntologyLoadConfig();
                config2.getAltNameSpaces().add("xenopus_developmental_stage");
                List<OntologyBulkLoadType> loadTypes = Arrays.asList(OntologyBulkLoadType.XBA, OntologyBulkLoadType.XBS);
                List<BaseOntologyTermService> services = Arrays.asList(xbaTermService, xbsTermService);
                List<GenericOntologyLoadConfig> configs = Arrays.asList(config, config2);
                processMultipleTerms(bulkLoadFile, loadTypes, services, configs);
            } else if(ontologyType == OntologyBulkLoadType.XPO) {
                config.setLoadOnlyIRIPrefix("XPO");
                processTerms(bulkLoadFile, ontologyType.getClazz(), xpoTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XBED) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), xbedTermService, config);
            } else if(ontologyType == OntologyBulkLoadType.XSMO) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), xsmoTermService, config);
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

    
    private <T extends OntologyTerm> void processTerms(BulkLoadFile bulkLoadFile, Class<T> clazz, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
        GenericOntologyLoadHelper<T> loader;
        if(config != null) {
            loader = new GenericOntologyLoadHelper<T>(clazz, config);
        } else {
            loader = new GenericOntologyLoadHelper<T>(clazz);
        }

        Map<String, T> termMap = loader.load(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));

        bulkLoadFile.setRecordCount(termMap.size());
        bulkLoadFileDAO.merge(bulkLoadFile);
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + clazz.getSimpleName() + " Database Persistance", termMap.size());
        for(String termKey: termMap.keySet()) {
            service.processUpdate(termMap.get(termKey));
            ph.progressProcess();
        }
        ph.finishProcess();
    }
    
    private <T extends OntologyTerm> void processMultipleTerms(BulkLoadFile bulkLoadFile, List<OntologyBulkLoadType> loadTypes, List<BaseOntologyTermService> services, List<GenericOntologyLoadConfig> configs) throws Exception {
        if (loadTypes.size() != services.size() || loadTypes.size() != configs.size()) {
            log.info("Unequal list lengths sent to processMultipleTerms - Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
            throw new Exception("Unequal list lengths sent to processMultipleTerms - Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
        }
        
        GenericOntologyLoadHelper<T> loader;
        bulkLoadFile.setRecordCount(0);
        
        for (int i = 0; i < loadTypes.size(); i++) {
            Class<T> clazz = (Class<T>) loadTypes.get(i).getClazz();
            if(configs.get(i) != null) {
                loader = new GenericOntologyLoadHelper<T>(clazz, configs.get(i));
            } else {
                loader = new GenericOntologyLoadHelper<T>(clazz);
            }

            Map<String, T> termMap = loader.load(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())));

            bulkLoadFile.setRecordCount(bulkLoadFile.getRecordCount() + termMap.size());
        
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + ": " + clazz.getSimpleName() + " Database Persistance", termMap.size());
            for(String termKey: termMap.keySet()) {
                services.get(i).processUpdate(termMap.get(termKey));
                ph.progressProcess();
            }
            ph.finishProcess();
        }
        bulkLoadFileDAO.merge(bulkLoadFile);
    }


}
