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
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.AGMPhenotypeAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AGMPhenotypeAnnotationService extends BaseAnnotationCrudService<AGMPhenotypeAnnotation, AGMPhenotypeAnnotationDAO> {

	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject AGMPhenotypeAnnotationFmsDTOValidator agmPhenotypeAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(agmPhenotypeAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> update(AGMPhenotypeAnnotation uiEntity) {
		return null;
	}

	@Override
	@Transactional
	public ObjectResponse<AGMPhenotypeAnnotation> create(AGMPhenotypeAnnotation uiEntity) {
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
	public ObjectResponse<AGMPhenotypeAnnotation> deleteById(Long id) {
		deprecateOrDeleteAnnotationAndNotes(id, true, "AGM phenotype annotation DELETE API call", false);
		ObjectResponse<AGMPhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return phenotypeAnnotationService.getAnnotationIdsByDataProvider(agmPhenotypeAnnotationDAO, dataProvider);
	}

	@Override
	public AGMPhenotypeAnnotation deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate) {
		return (AGMPhenotypeAnnotation) phenotypeAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, throwApiError, loadDescription, deprecate);
	}
}
