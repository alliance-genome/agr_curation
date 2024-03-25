package org.alliancegenome.curation_api.services;

import java.util.List;

import org.alliancegenome.curation_api.dao.GenePhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.GenePhenotypeAnnotationFmsDTOValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GenePhenotypeAnnotationService extends BaseEntityCrudService<GenePhenotypeAnnotation, GenePhenotypeAnnotationDAO> {

	@Inject
	GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject
	GenePhenotypeAnnotationFmsDTOValidator genePhenotypeAnnotationFmsDtoValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(genePhenotypeAnnotationDAO);
	}

	public ObjectResponse<GenePhenotypeAnnotation> get(String identifier) {
		SearchResponse<GenePhenotypeAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GenePhenotypeAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<GenePhenotypeAnnotation>(ret.getResults().get(0));
		
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
	public ObjectResponse<GenePhenotypeAnnotation> update(GenePhenotypeAnnotation uiEntity) {
		return null;
	}

	@Override
	@Transactional
	public ObjectResponse<GenePhenotypeAnnotation> create(GenePhenotypeAnnotation uiEntity) {
		return null;
	}

	@Transactional
	public GenePhenotypeAnnotation upsertPrimaryAnnotation(Gene subject, PhenotypeFmsDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		GenePhenotypeAnnotation annotation = genePhenotypeAnnotationFmsDtoValidator.validatePrimaryAnnotation(subject, dto, dataProvider);
		return genePhenotypeAnnotationDAO.persist(annotation);
	}
	
	@Override
	@Transactional
	public ObjectResponse<GenePhenotypeAnnotation> delete(Long id) {
		phenotypeAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "Gene phenotype annotation DELETE API call", false);
		ObjectResponse<GenePhenotypeAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		return phenotypeAnnotationService.getAnnotationIdsByDataProvider(genePhenotypeAnnotationDAO, dataProvider);
	}
}
