package org.alliancegenome.curation_api.services;

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

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

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

    @Transactional
    public void upsert(String entityId, String doTermId, String publicationId) {

        BiologicalEntity entity = biologicalEntityDAO.find(entityId);

        // do not create DA if no entity / subject is found.
        if (entity == null) return;

        DOTerm disease = doTermDAO.find(doTermId);
        // TODo: Change logic when ontology loader is in place
        // do not create new DOTerm records here
        // but raise an error if disease cannot be found
        if (disease == null) {
            disease = new DOTerm();
            disease.setCurie(doTermId);
            doTermDAO.persist(disease);
        }
        Reference reference = referenceDAO.find(publicationId);
        if (reference == null) {
            reference = new Reference();
            reference.setCurie(publicationId);
            // ToDo: need this until references are loaded separately
            // raise an error when reference cannot be found?
            referenceDAO.persist(reference);
        }

        DiseaseAnnotation da = new DiseaseAnnotation();

        da.setSubject(entity);
        da.setObject(disease);
        da.setReferenceList(List.of(reference));
        create(da);

    }


}
