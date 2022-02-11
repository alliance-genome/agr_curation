package org.alliancegenome.curation_api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.GeneDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.DiseaseRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.DiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.services.helpers.validators.GeneDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneDiseaseAnnotationCrudService extends BaseCrudService<GeneDiseaseAnnotation, GeneDiseaseAnnotationDAO> {

    @Inject
    GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
    
    @Inject
    GeneDAO geneDAO;
    
    @Inject
    GeneDiseaseAnnotationValidator geneDiseaseValidator;

    @Inject
    DiseaseAnnotationService diseaseAnnotationService;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDiseaseAnnotationDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<GeneDiseaseAnnotation> update(GeneDiseaseAnnotation uiEntity) {
        GeneDiseaseAnnotation dbEntity = geneDiseaseValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<GeneDiseaseAnnotation>(geneDiseaseAnnotationDAO.persist(dbEntity));
    }

    @Transactional
    public void runLoad(String taxonId, List<GeneDiseaseAnnotationDTO> annotations) {
        List<String> annotationIdsBefore = new ArrayList<>();
        annotationIdsBefore.addAll(geneDiseaseAnnotationDAO.findAllAnnotationIds(taxonId));
        annotationIdsBefore.removeIf(Objects::isNull);
        
        log.debug("runLoad: Before: " + taxonId + " " + annotationIdsBefore.size());
        List<String> annotationIdsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonId, annotations.size());
        annotations.forEach(annotationDTO -> {
            GeneDiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null) {
                annotationIdsAfter.add(annotation.getUniqueId());
            }
            ph.progressProcess();
        });
        ph.finishProcess();
        
        diseaseAnnotationService.removeNonUpdatedAnnotations(taxonId, annotationIdsBefore, annotationIdsAfter);
    }

    private GeneDiseaseAnnotation upsert(GeneDiseaseAnnotationDTO dto) {
        GeneDiseaseAnnotation annotation = validateGeneDiseaseAnnotationDTO(dto);
        if (annotation == null) return null;
        
        annotation = (GeneDiseaseAnnotation) diseaseAnnotationService.upsert(annotation, dto);
        if (annotation != null) {
            geneDiseaseAnnotationDAO.persist(annotation);
        }
        return annotation;
    }
    
    private GeneDiseaseAnnotation validateGeneDiseaseAnnotationDTO(GeneDiseaseAnnotationDTO dto) {
        GeneDiseaseAnnotation annotation;
        if (dto.getSubject() == null) {
            log("Annotation for " + dto.getObject() + " missing a subject AGM - skipping");
            return null;
        }
        
        Gene gene = geneDAO.find(dto.getSubject());
        if (gene == null) {
            log("Allele " + dto.getSubject() + " not found in database - skipping annotation");
            return null;
        }
        
        String annotationId = dto.getModId();
        if (annotationId == null) {
            annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(gene.getTaxon().getCurie()).getCurieID(dto);
        }
        SearchResponse<GeneDiseaseAnnotation> annotationList = geneDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
        if (annotationList == null || annotationList.getResults().size() == 0) {
            annotation = new GeneDiseaseAnnotation();
            annotation.setUniqueId(annotationId);
            annotation.setSubject(gene);
        } else {
            annotation = annotationList.getResults().get(0);
        }
        
        if (!dto.getDiseaseRelation().equals("is_implicated_in") &&
                !dto.getDiseaseRelation().equals("is_marker_for")) {
            log("Invalid gene disease relation for " + annotationId + " - skipping");
            return null;
        }
        annotation.setDiseaseRelation(DiseaseRelation.valueOf(dto.getDiseaseRelation()));
        
        
        return (GeneDiseaseAnnotation) diseaseAnnotationService.validateAnnotationDTO(annotation, dto);
    }
    
    private void log(String message) {
        log.debug(message);
        // log.info(message);
    }
}
