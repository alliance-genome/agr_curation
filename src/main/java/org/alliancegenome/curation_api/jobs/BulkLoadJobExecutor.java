package org.alliancegenome.curation_api.jobs;

import java.io.FileInputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
import static org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType.*;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.IngestDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;
import org.alliancegenome.curation_api.services.*;
import org.alliancegenome.curation_api.services.helpers.*;
import org.alliancegenome.curation_api.services.ontology.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@ApplicationScoped
public class BulkLoadJobExecutor {

    @Inject ObjectMapper mapper;

    @Inject AlleleService alleleService;
    @Inject GeneService geneService;
    @Inject AffectedGenomicModelService agmService;
    @Inject DiseaseAnnotationFmsService diseaseFmsService;
    @Inject AGMDiseaseAnnotationCrudService agmDiseaseService;
    @Inject AlleleDiseaseAnnotationCrudService alleleDiseaseService;
    @Inject GeneDiseaseAnnotationCrudService geneDiseaseService;

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

    @Inject MoleculeService moleculeService;

    @Inject BulkLoadFileDAO bulkLoadFileDAO;
    




    public void process(BulkLoadFile bulkLoadFile) throws Exception {

        BackendBulkLoadType loadType = bulkLoadFile.getBulkLoad().getBackendBulkLoadType();
        
        List<BackendBulkLoadType> ingestTypes = List.of(
            AGM_DISEASE_ANNOTATION,
            ALLELE_DISEASE_ANNOTATION,
            GENE_DISEASE_ANNOTATION,
            DISEASE_ANNOTATION,
            FULL_INGEST
        );

        if (ingestTypes.contains(loadType)) {
            
            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);

            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + manual.getDataType().getTaxonId());
            bulkLoadFile.setRecordCount(0);
            bulkLoadFileDAO.merge(bulkLoadFile);
            
            if (loadType == ALLELE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                if (ingestDto.getDiseaseAlleleIngestSet() != null) {
                    bulkLoadFile.setRecordCount(ingestDto.getDiseaseAlleleIngestSet().size() + bulkLoadFile.getRecordCount());
                    bulkLoadFileDAO.merge(bulkLoadFile);
                    alleleDiseaseService.runLoad(manual.getDataType().getTaxonId(), ingestDto.getDiseaseAlleleIngestSet());
                }
            }
            if (loadType == AGM_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                if (ingestDto.getDiseaseAgmIngestSet() != null) {
                    bulkLoadFile.setRecordCount(ingestDto.getDiseaseAgmIngestSet().size() + bulkLoadFile.getRecordCount());
                    bulkLoadFileDAO.merge(bulkLoadFile);
                    agmDiseaseService.runLoad(manual.getDataType().getTaxonId(), ingestDto.getDiseaseAgmIngestSet());
                }
            }
            if (loadType == GENE_DISEASE_ANNOTATION || loadType == DISEASE_ANNOTATION || loadType == FULL_INGEST) {
                if (ingestDto.getDiseaseGeneIngestSet() != null) {
                    bulkLoadFile.setRecordCount(ingestDto.getDiseaseGeneIngestSet().size() + bulkLoadFile.getRecordCount());
                    bulkLoadFileDAO.merge(bulkLoadFile);
                    geneDiseaseService.runLoad(manual.getDataType().getTaxonId(), ingestDto.getDiseaseGeneIngestSet());
                }
            }
            
            // Other items to be added here later

        }


        if(loadType == BackendBulkLoadType.GENE_DTO) {
            GeneMetaDataFmsDTO geneData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(geneData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processGeneDTOData(bulkLoadFile, geneData);
        } else if(loadType == BackendBulkLoadType.ALLELE_DTO) {
            AlleleMetaDataFmsDTO alleleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AlleleMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(alleleData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processAlleleDTOData(bulkLoadFile, alleleData);
        } else if(loadType == BackendBulkLoadType.AGM_DTO) {
            AffectedGenomicModelMetaDataFmsDTO agmData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModelMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(agmData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processAGMDTOData(bulkLoadFile, agmData);
        } else if(loadType == BackendBulkLoadType.DISEASE_ANNOTATION_DTO) {
            DiseaseAnnotationMetaDataFmsDTO diseaseData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), DiseaseAnnotationMetaDataFmsDTO.class);
            BulkFMSLoad fms = (BulkFMSLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + fms.getDataSubType() + " " + BackendBulkDataType.valueOf(fms.getDataSubType()));
            bulkLoadFile.setRecordCount(diseaseData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            diseaseFmsService.runLoad(BackendBulkDataType.valueOf(fms.getDataSubType()).getTaxonId(), diseaseData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.MOLECULE) {
            MoleculeMetaDataFmsDTO moleculeData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), MoleculeMetaDataFmsDTO.class);
            BulkFMSLoad fms = (BulkFMSLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + fms.getDataSubType() + " " + fms.getDataSubType());
            bulkLoadFile.setRecordCount(moleculeData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processMoleculeDTOData(bulkLoadFile, moleculeData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ONTOLOGY) {
            GenericOntologyLoadConfig config = new GenericOntologyLoadConfig();
            BaseOntologyTermService service = null;
            OntologyBulkLoadType ontologyType = bulkLoadFile.getBulkLoad().getOntologyType();
            
            if(ontologyType == OntologyBulkLoadType.ZECO) {
                config.setLoadOnlyIRIPrefix("ZECO");
                service = zecoTermService;
            } else if(ontologyType == OntologyBulkLoadType.EMAPA) {
                config.getAltNameSpaces().add("anatomical_structure");
                service = emapaTermService;
            } else if(ontologyType == OntologyBulkLoadType.GO) {
                config.getAltNameSpaces().add("biological_process");
                config.getAltNameSpaces().add("molecular_function");
                config.getAltNameSpaces().add("cellular_component");
                service = goTermService;
            } else if(ontologyType == OntologyBulkLoadType.SO) {
                service = soTermService;
            } else if(ontologyType == OntologyBulkLoadType.XCO) {
                service = xcoTermService;
            } else if(ontologyType == OntologyBulkLoadType.ECO) {
                service = ecoTermService;
            } else if(ontologyType == OntologyBulkLoadType.CHEBI) {
                service = chebiTermService;
            } else if(ontologyType == OntologyBulkLoadType.ZFA) {
                config.getAltNameSpaces().add("zebrafish_anatomy");
                service = zfaTermService;
            } else if(ontologyType == OntologyBulkLoadType.DO) {
                service = doTermService;
            } else if(ontologyType == OntologyBulkLoadType.MP) {
                config.setLoadOnlyIRIPrefix("MP");
                service = mpTermService;
            } else if(ontologyType == OntologyBulkLoadType.MA) {
                service = maTermService;
            } else if(ontologyType == OntologyBulkLoadType.WBBT) {
                service = wbbtTermService;
            } else if(ontologyType == OntologyBulkLoadType.DAO) {
                config.setLoadOnlyIRIPrefix("FBbt");
                service = daoTermService;
            } else {
                log.info("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
                throw new Exception("Ontolgy Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + ontologyType + " not implemented");
            }

            if(service != null) {
                processTerms(bulkLoadFile, ontologyType.getClazz(), bulkLoadFile.getLocalFilePath(), service, config);
                if(ontologyType == OntologyBulkLoadType.ECO) {
                    ecoTermService.updateAbbreviations();
                }
            }
        } else {
            log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
            throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
        }
        log.info("Process Finished for: " + bulkLoadFile);
    }

    public void processAlleleDTOData(BulkLoadFile bulkLoadFile, AlleleMetaDataFmsDTO alleleData) {
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        if(bulkLoadFile == null) {
            ph.startProcess("Allele DTO Update", alleleData.getData().size());
        } else {
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " Allele DTO Update", alleleData.getData().size());
        }
        for(AlleleFmsDTO allele: alleleData.getData()) {
            alleleService.processUpdate(allele);
            ph.progressProcess();
        }
        ph.finishProcess();
    }

    public void processMoleculeDTOData(BulkLoadFile bulkLoadFile, MoleculeMetaDataFmsDTO moleculeData) {
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        if(bulkLoadFile == null) {
            ph.startProcess("Molecule Update", moleculeData.getData().size());
        } else {
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " Molecule DTO Update", moleculeData.getData().size());
        }
        for(MoleculeFmsDTO molecule: moleculeData.getData()) {
            moleculeService.processUpdate(molecule);
            ph.progressProcess();
        }
        ph.finishProcess();
    }

    public void processGeneDTOData(BulkLoadFile bulkLoadFile, GeneMetaDataFmsDTO geneData) {
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        if(bulkLoadFile == null) {
            ph.startProcess("Gene DTO Update", geneData.getData().size());
        } else {
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " Gene DTO Update", geneData.getData().size());
        }
        for(GeneFmsDTO gene: geneData.getData()) {
            geneService.processUpdate(gene);
            ph.progressProcess();
        }
        ph.finishProcess();
    }

    public void processAGMDTOData(BulkLoadFile bulkLoadFile, AffectedGenomicModelMetaDataFmsDTO agmData) {
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        if(bulkLoadFile == null) {
            ph.startProcess("AGM DTO Update", agmData.getData().size());
        } else {
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " AGM DTO Update", agmData.getData().size());
        }
        for(AffectedGenomicModelFmsDTO agm: agmData.getData()) {
            agmService.processUpdate(agm);
            ph.progressProcess();
        }
        ph.finishProcess();
    }

    private <T extends OntologyTerm> void processTerms(BulkLoadFile bulkLoadFile, Class<T> clazz, String filePath, BaseOntologyTermService service) throws Exception {
        processTerms(bulkLoadFile, clazz, filePath, service, null);
    }

    private <T extends OntologyTerm> void processTerms(BulkLoadFile bulkLoadFile, Class<T> clazz, String filePath, BaseOntologyTermService service, GenericOntologyLoadConfig config) throws Exception {
        GenericOntologyLoadHelper<T> loader;
        if(config != null) {
            loader = new GenericOntologyLoadHelper<T>(clazz, config);
        } else {
            loader = new GenericOntologyLoadHelper<T>(clazz);
        }

        Map<String, T> termMap = loader.load(new GZIPInputStream(new FileInputStream(filePath)));

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


}
