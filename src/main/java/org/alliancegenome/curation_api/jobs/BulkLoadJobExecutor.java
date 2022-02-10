package org.alliancegenome.curation_api.jobs;

import java.io.FileInputStream;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.loads.BulkLoadFileDAO;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.*;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BackendBulkLoadType;
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
    

    private Map<String, String> modTaxons = Map.of(
            "HUMAN", "NCBITaxon:9606",
            "FB", "NCBITaxon:7227",
            "MGI", "NCBITaxon:10090",
            "RGD", "NCBITaxon:10116",
            "SGD", "NCBITaxon:559292",
            "WB", "NCBITaxon:6239",
            "ZFIN", "NCBITaxon:7955"
            );  


    public void process(BulkLoadFile bulkLoadFile) throws Exception {
        //log.info("Process Starting for: " + bulkLoadFile);
        if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GENE_DTO) {
            GeneMetaDataFmsDTO geneData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), GeneMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(geneData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processGeneDTOData(bulkLoadFile, geneData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ALLELE_DTO) {
            AlleleMetaDataFmsDTO alleleData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AlleleMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(alleleData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processAlleleDTOData(bulkLoadFile, alleleData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.AGM_DTO) {
            AffectedGenomicModelMetaDataFmsDTO agmData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModelMetaDataFmsDTO.class);
            bulkLoadFile.setRecordCount(agmData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            processAGMDTOData(bulkLoadFile, agmData);

        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GENE) {    
            Gene[] genes = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), Gene[].class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            bulkLoadFile.setRecordCount(genes.length);
            bulkLoadFileDAO.merge(bulkLoadFile);
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " Gene Update", genes.length);
            for(Gene gene: genes) {
                geneService.update(gene);
                ph.progressProcess();
            }
            ph.finishProcess();
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ALLELE) {  
            Allele[] alleles = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), Allele[].class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            bulkLoadFile.setRecordCount(alleles.length);
            bulkLoadFileDAO.merge(bulkLoadFile);
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " Allele Update", alleles.length);
            for(Allele allele: alleles) {
                alleleService.update(allele);
                ph.progressProcess();
            }
            ph.finishProcess();

        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.AGM) { 
            AffectedGenomicModel[] agms = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), AffectedGenomicModel[].class);
            ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
            bulkLoadFile.setRecordCount(agms.length);
            bulkLoadFileDAO.merge(bulkLoadFile);
            ph.startProcess(bulkLoadFile.getBulkLoad().getName() + " AGM Update", agms.length);
            for(AffectedGenomicModel agm: agms) {
                agmService.update(agm);
                ph.progressProcess();
            }
            ph.finishProcess();

        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.DISEASE_ANNOTATION_DTO) {
            DiseaseAnnotationMetaDataFmsDTO diseaseData = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), DiseaseAnnotationMetaDataFmsDTO.class);
            // TODO find taxon ID and send it with this load
            BulkFMSLoad fms = (BulkFMSLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + fms.getDataSubType() + " " + modTaxons.get(fms.getDataSubType()));

            bulkLoadFile.setRecordCount(diseaseData.getData().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            diseaseFmsService.runLoad(modTaxons.get(fms.getDataSubType()), diseaseData);
        } else if(bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.DISEASE_ANNOTATION) {
            IngestDTO ingestDto= mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + modTaxons.get(manual.getDataType().name()));
            bulkLoadFile.setRecordCount(ingestDto.getDiseaseAgmIngestSet().size() + ingestDto.getDiseaseAlleleIngestSet().size() + ingestDto.getDiseaseGeneIngestSet().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            if (ingestDto.getDiseaseAgmIngestSet() != null) {
                agmDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseAgmIngestSet());
            }
            if (ingestDto.getDiseaseAlleleIngestSet() != null) {
                alleleDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseAlleleIngestSet());
            }
            if (ingestDto.getDiseaseGeneIngestSet() != null) {
                geneDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseGeneIngestSet());
            }
        } else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.AGM_DISEASE_ANNOTATION) {
            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + modTaxons.get(manual.getDataType().name()));
            bulkLoadFile.setRecordCount(ingestDto.getDiseaseAgmIngestSet().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            if (ingestDto.getDiseaseAgmIngestSet() != null) {
                agmDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseAgmIngestSet());
            }
        } else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.ALLELE_DISEASE_ANNOTATION) {
            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + modTaxons.get(manual.getDataType().name()));
            bulkLoadFile.setRecordCount(ingestDto.getDiseaseAlleleIngestSet().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            if (ingestDto.getDiseaseAlleleIngestSet() != null) {
                alleleDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseAlleleIngestSet());
            }
        } else if (bulkLoadFile.getBulkLoad().getBackendBulkLoadType() == BackendBulkLoadType.GENE_DISEASE_ANNOTATION) {
            IngestDTO ingestDto = mapper.readValue(new GZIPInputStream(new FileInputStream(bulkLoadFile.getLocalFilePath())), IngestDTO.class);
            BulkManualLoad manual = (BulkManualLoad)bulkLoadFile.getBulkLoad();
            log.info("Running with: " + manual.getDataType().name() + " " + modTaxons.get(manual.getDataType().name()));
            bulkLoadFile.setRecordCount(ingestDto.getDiseaseGeneIngestSet().size());
            bulkLoadFileDAO.merge(bulkLoadFile);
            if (ingestDto.getDiseaseGeneIngestSet() != null) {
                geneDiseaseService.runLoad(modTaxons.get(manual.getDataType().name()), ingestDto.getDiseaseGeneIngestSet());
            }
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
            if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.ZECO) {
                config.setLoadOnlyIRIPrefix("ZECO");
                service = zecoTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.EMAPA) {
                config.getAltNameSpaces().add("anatomical_structure");
                service = emapaTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.GO) {
                config.getAltNameSpaces().add("biological_process");
                config.getAltNameSpaces().add("molecular_function");
                config.getAltNameSpaces().add("cellular_component");
                service = goTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.SO) {
                service = soTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.XCO) {
                service = xcoTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.ECO) {
                service = ecoTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.CHEBI) {
                service = chebiTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.ZFA) {
                config.getAltNameSpaces().add("zebrafish_anatomy");
                service = zfaTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.DO) {
                service = doTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.MP) {
                config.setLoadOnlyIRIPrefix("MP");
                service = mpTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.MA) {
                service = maTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.WBBT) {
                service = wbbtTermService;
            } else if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.DAO) {
                config.setLoadOnlyIRIPrefix("FBbt");
                service = daoTermService;
            } else {
                log.info("Ontology Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + bulkLoadFile.getBulkLoad().getOntologyType() + " not implemented");
                throw new Exception("Ontolgy Load: " + bulkLoadFile.getBulkLoad().getName() + " for OT: " + bulkLoadFile.getBulkLoad().getOntologyType() + " not implemented");
            }

            if(service != null) {
                processTerms(bulkLoadFile, bulkLoadFile.getBulkLoad().getOntologyType().getClazz(), bulkLoadFile.getLocalFilePath(), service, config);
                if(bulkLoadFile.getBulkLoad().getOntologyType() == OntologyBulkLoadType.ECO) {
                    ecoTermService.updateAbbreviations();
                }
            }


        } else if(bulkLoadFile.getBulkLoad().getGroup().getName().equals("Reindex Tasks")) {
            if(bulkLoadFile.getBulkLoad().getName().equals("Gene Reindex")) { geneService.reindex(); }
            if(bulkLoadFile.getBulkLoad().getName().equals("Allele Reindex")) { alleleService.reindex(); }
            if(bulkLoadFile.getBulkLoad().getName().equals("AGM Reindex")) { agmService.reindex(); }
        } else {
            log.info("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
            throw new Exception("Load: " + bulkLoadFile.getBulkLoad().getName() + " not implemented");
        }

        //log.info("Process Finished for: " + bulkLoadFile);
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
