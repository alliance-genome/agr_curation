package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.AGMPhenotypeAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AGMPhenotypeAnnotationService extends BaseEntityCrudService<AGMPhenotypeAnnotation, AGMPhenotypeAnnotationDAO> {

	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	//@Inject
	//AGMPhenotypeAnnotationValidator agmPhenotypeValidator;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject
	AGMPhenotypeAnnotationFmsDTOValidator agmPhenotypeAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmPhenotypeAnnotationDAO);
	}

	public ObjectResponse<AGMPhenotypeAnnotation> get(String identifier) {
		SearchResponse<AGMPhenotypeAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMPhenotypeAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMPhenotypeAnnotation>(ret.getResults().get(0));
		
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
	public ObjectResponse<AGMPhenotypeAnnotation> update(AGMPhenotypeAnnotation uiEntity) {
		//AGMPhenotypeAnnotation dbEntity = agmPhenotypeValidator.validateAnnotationUpdate(uiEntity);
		//return new ObjectResponse<>(agmPhenotypeAnnotationDAO.persist(dbEntity));
		return null;
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> create(AGMPhenotypeAnnotation uiEntity) {
		//AGMPhenotypeAnnotation dbEntity = agmPhenotypeValidator.validateAnnotationCreate(uiEntity);
		//return new ObjectResponse<>(agmPhenotypeAnnotationDAO.persist(dbEntity));
		return null;
	}

	@Transactional
	public AGMPhenotypeAnnotation upsertPrimaryAnnotation(AffectedGenomicModel subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationFmsDtoValidator.validatePrimaryAnnotation(subject, dto, dataProvider);

		return agmPhenotypeAnnotationDAO.persist(annotation);
	}
	
	@Transactional
	public void addInferredOrAssertedEntities(AffectedGenomicModel primaryAnnotationSubject, PhenotypeFmsDTO secondaryAnnotationDto, List<Long> idsAdded, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AGMPhenotypeAnnotation annotation = agmPhenotypeAnnotationFmsDtoValidator.validateInferredOrAssertedEntities(primaryAnnotationSubject, secondaryAnnotationDto, idsAdded, dataProvider);
		agmPhenotypeAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> delete(Long id) {
		phenotypeAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "AGM phenotype annotation DELETE API call", false);
		ObjectResponse<AGMPhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return phenotypeAnnotationService.getAnnotationIdsByDataProvider(agmPhenotypeAnnotationDAO, dataProvider);
	}
}
