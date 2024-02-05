package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AGMDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.AGMDiseaseAnnotationDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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
	public ObjectResponse<AGMDiseaseAnnotation> get(String identifier) {
		SearchResponse<AGMDiseaseAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<>(ret.getResults().get(0));
				
		return new ObjectResponse<>();
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
	public AGMDiseaseAnnotation upsert(AGMDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AGMDiseaseAnnotation annotation = agmDiseaseAnnotationDtoValidator.validateAGMDiseaseAnnotationDTO(dto, dataProvider);

		return agmDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "AGM disease annotation DELETE API call", false);
		ObjectResponse<AGMDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String loadDescription) { }

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return diseaseAnnotationService.getAnnotationIdsByDataProvider(agmDiseaseAnnotationDAO, dataProvider);
	}
}
