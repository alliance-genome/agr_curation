package org.alliancegenome.curation_api.services.helpers.diseaseAnnotations;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.BiologicalEntityDAO;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.DoTermDAO;
import org.alliancegenome.curation_api.dao.ReferenceDAO;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseAnnotationMetaDataDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.DiseaseModelAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.ExperimentalConditionDTO;
import org.alliancegenome.curation_api.services.CurieGenerator;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        if (annotation == null) {
            DiseaseAnnotation da = new DiseaseAnnotation();
            da.setCurie(annotationID);
            da.setSubject(entity);
            da.setObject(disease);
            da.setReferenceList(List.of(reference));
            return create(da);
        } else {
            // logic for updates
            // currently no fields persisted in PG that need to be updated, only fields that make the unique key, i.e.
            // it would be a new annotation.
        }
        return annotation;
    }


    @Transactional
    public DiseaseAnnotation upsert(DiseaseModelAnnotationDTO annotationDTO) {
        String entityId = annotationDTO.getObjectId();

        BiologicalEntity entity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (entity == null) return null;

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
        ph.startProcess("Disease Annotation Update", annotationData.getData().size());
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
