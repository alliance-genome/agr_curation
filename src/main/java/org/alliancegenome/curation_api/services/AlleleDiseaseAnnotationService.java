package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;
import org.alliancegenome.curation_api.services.validation.AlleleDiseaseAnnotationValidator;
import org.alliancegenome.curation_api.services.validation.dto.AlleleDiseaseAnnotationDTOValidator;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class AlleleDiseaseAnnotationService extends BaseDTOCrudService<AlleleDiseaseAnnotation, AlleleDiseaseAnnotationDTO, AlleleDiseaseAnnotationDAO> {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationValidator alleleDiseaseValidator;
	@Inject
	AlleleDiseaseAnnotationDTOValidator alleleDiseaseAnnotationDtoValidator;
	@Inject
	DiseaseAnnotationService diseaseAnnotationService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(alleleDiseaseAnnotationDAO);
	}

	@Override
	public ObjectResponse<AlleleDiseaseAnnotation> get(String identifier) {
		SearchResponse<AlleleDiseaseAnnotation> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AlleleDiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<AlleleDiseaseAnnotation>();
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> update(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationUpdate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> create(AlleleDiseaseAnnotation uiEntity) {
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseValidator.validateAnnotationCreate(uiEntity);
		return new ObjectResponse<>(alleleDiseaseAnnotationDAO.persist(dbEntity));
	}

	@Transactional
	public AlleleDiseaseAnnotation upsert(AlleleDiseaseAnnotationDTO dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		AlleleDiseaseAnnotation annotation = alleleDiseaseAnnotationDtoValidator.validateAlleleDiseaseAnnotationDTO(dto, dataProvider);

		return alleleDiseaseAnnotationDAO.persist(annotation);
	}

	@Override
	@Transactional
	public ObjectResponse<AlleleDiseaseAnnotation> delete(Long id) {
		diseaseAnnotationService.deprecateOrDeleteAnnotationAndNotes(id, true, "Allele disease annotation DELETE API call", false);
		ObjectResponse<AlleleDiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if(StringUtils.equals(dataProvider.sourceOrganization, "RGD"))
			params.put(EntityFieldConstants.SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		List<Long> annotationIds = alleleDiseaseAnnotationDAO.findFilteredIds(params);
		annotationIds.removeIf(Objects::isNull);
		
		return annotationIds;
	}
}
