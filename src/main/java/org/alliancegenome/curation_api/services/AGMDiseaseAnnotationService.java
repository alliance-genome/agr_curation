package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AGMDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.AGMDiseaseAnnotationDTOValidator;

@RequestScoped
public class AGMDiseaseAnnotationService extends BaseDTOCrudService<AGMDiseaseAnnotation, AGMDiseaseAnnotationDTO, AGMDiseaseAnnotationDAO> {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	AGMDiseaseAnnotationValidator agmDiseaseValidator;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;
	@Inject
	AGMDiseaseAnnotationDTOValidator agmDiseaseAnnotationDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmDiseaseAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> update(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> create(AGMDiseaseAnnotation uiEntity) {
		AGMDiseaseAnnotation dbEntity = agmDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(agmDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto, String dataType) throws ObjectUpdateException {
		AGMDiseaseAnnotation annotation = agmDiseaseAnnotationDtoValidator.validateAGMDiseaseAnnotationDTO(dto);

		return agmDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "disease annotation", false);
		ObjectResponse<AGMDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String dataType) { }
}
