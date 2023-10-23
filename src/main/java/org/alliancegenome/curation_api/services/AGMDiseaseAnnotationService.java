package org.alliancegenome.curation_api.services;

import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

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
import org.apache.commons.lang.StringUtils;

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
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<AGMDiseaseAnnotation>(ret.getResults().get(0));
				
		return new ObjectResponse<AGMDiseaseAnnotation>();
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
	public void removeOrDeprecateNonUpdated(String curie, String dataProviderName, String md5sum) { }

	public List<Long> getAnnotationIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		List<Long> annotationIds;

		String sourceOrg = dataProvider.sourceOrganization;

		if( StringUtils.equals(sourceOrg, "RGD") ){
			annotationIds = agmDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider.sourceOrganization, dataProvider.canonicalTaxonCurie);
		} else {
			annotationIds = agmDiseaseAnnotationDAO.findAllAnnotationIdsByDataProvider(dataProvider.sourceOrganization);
		}

		annotationIds.removeIf(Objects::isNull);

		return annotationIds;
	}
}
