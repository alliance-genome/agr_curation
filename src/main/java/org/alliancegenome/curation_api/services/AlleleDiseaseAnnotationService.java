package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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

	@Override
	public void removeOrDeprecateNonUpdated(String curie, String loadDescription) { }

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put("dataProvider.sourceOrganization.abbreviation", dataProvider.sourceOrganization);
		if(StringUtils.equals(dataProvider.sourceOrganization, "RGD"))
			params.put("subject.taxon.curie", dataProvider.canonicalTaxonCurie);
		List<String> annotationIdStrings = alleleDiseaseAnnotationDAO.findFilteredIds(params);
		annotationIdStrings.removeIf(Objects::isNull);
		List<Long> annotationIds = annotationIdStrings.stream().map(Long::parseLong).collect(Collectors.toList());
		
		return annotationIds;
	}
}
