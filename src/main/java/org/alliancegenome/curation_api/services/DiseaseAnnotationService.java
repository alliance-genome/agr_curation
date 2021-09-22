package org.alliancegenome.curation_api.services;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class DiseaseAnnotationService extends BaseService<DiseaseAnnotation, DiseaseAnnotationDAO> {

    @Inject
    DiseaseAnnotationDAO diseaseAnnotationDAO;
    @Inject
    ReferenceDAO referenceDAO;
    @Inject
    DoTermDAO doTermDAO;
    @Inject
    BiologicalEntityDAO biologicalEntityDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(diseaseAnnotationDAO);
    }

    private DiseaseAnnotation upsertAnnotation(DiseaseModelAnnotationDTO annotationDTO, BiologicalEntity entity, DOTerm disease, Reference reference) {

        String annotationID = getUniqueID(annotationDTO);
        DiseaseAnnotation annotation = diseaseAnnotationDAO.find(annotationID);
        boolean create = false;

        if (annotation == null) {
            annotation = new DiseaseAnnotation();
            annotation.setCurie(annotationID);
            create = true;
        }
        annotation.setSubject(entity);
        annotation.setObject(disease);
        annotation.setReferenceList(List.of(reference));
        annotation.setNegated(annotationDTO.getNegation() == DiseaseModelAnnotationDTO.Negation.not);
        annotation.setCreated(
                    annotationDTO.getDateAssigned()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        annotation.setDiseaseRelation(DiseaseAnnotation.DiseaseRelation.valueOf(
                annotationDTO
                        .getObjectRelation()
                        .getAssociationType())
        );

        if(create){
            create(annotation);
        }else{
            update(annotation);
        }

        return annotation;
    }

    @Transactional
    public DiseaseAnnotation upsert(DiseaseModelAnnotationDTO annotationDTO) {
        String entityId = annotationDTO.getObjectId();

        BiologicalEntity entity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (entity == null) return null;

        // if there are primary genetic entity IDs available it is an
        // inferred annotation. Skip it then.
        if(CollectionUtils.isNotEmpty(annotationDTO.getPrimaryGeneticEntityIDs()))
            return null;

        String doTermId = annotationDTO.getDoId();
        DOTerm disease = doTermDAO.find(doTermId);
        // TODo: Change logic when ontology loader is in place
        // do not create new DOTerm records here
        // but raise an error if disease cannot be found
        if (disease == null) {
            disease = new DOTerm();
            disease.setCurie(doTermId);
            doTermDAO.persist(disease);
        }

        String publicationId = annotationDTO.getEvidence().getPublication().getPublicationId();
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }
        return upsertAnnotation(annotationDTO, entity, disease, reference);
    }

    private String getUniqueID(DiseaseModelAnnotationDTO annotationDTO) {

        BiologicalEntity entity = biologicalEntityDAO.find(annotationDTO.getObjectId());

        // do not create DA if no entity / subject is found.
        if (entity == null)
            return null;

        return DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(entity.getTaxon()).getCurieID(annotationDTO);
    }

    public void runLoad(String taxonID, DiseaseAnnotationMetaDataDTO annotationData) {
        List<String> annotationsIDsBefore = diseaseAnnotationDAO.findAllAnnotationIDs(taxonID);
        List<String> annotationsIDsAfter = new ArrayList<>();
        ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
        ph.startProcess("Disease Annotation Update " + taxonID, annotationData.getData().size());
        annotationData.getData().forEach(annotationDTO -> {
            DiseaseAnnotation annotation = upsert(annotationDTO);
            if (annotation != null)
                annotationsIDsAfter.add(annotation.getCurie());
            ph.progressProcess();
        });
        ph.finishProcess();

        List<String> distinctAfter = annotationsIDsAfter.stream().distinct().collect(Collectors.toList());
        List<String> idsToRemove = ListUtils.subtract(annotationsIDsBefore, distinctAfter);
        idsToRemove.forEach(this::delete);
    }
}
