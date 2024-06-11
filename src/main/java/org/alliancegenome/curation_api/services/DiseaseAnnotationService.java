package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.DiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseAnnotationCrudService;
import org.alliancegenome.curation_api.services.helpers.annotations.DiseaseAnnotationUniqueIdUpdateHelper;
import org.apache.commons.lang.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class DiseaseAnnotationService extends BaseAnnotationCrudService<DiseaseAnnotation, DiseaseAnnotationDAO> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;
	@Inject PersonService personService;
	@Inject DiseaseAnnotationUniqueIdUpdateHelper uniqueIdUpdateHelper;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(diseaseAnnotationDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<DiseaseAnnotation> deleteById(Long id) {
		deprecateOrDelete(id, true, "Disease annotation DELETE API call", false);
		ObjectResponse<DiseaseAnnotation> ret = new ObjectResponse<>();
		return ret;
	}

	public void updateUniqueIds() {
		uniqueIdUpdateHelper.updateDiseaseAnnotationUniqueIds();
	}

	public List<Long> getAllReferencedConditionRelationIds() {
		return getAllReferencedConditionRelationIds(diseaseAnnotationDAO);
	}

	protected <D extends BaseSQLDAO<?>> List<Long> getAnnotationIdsByDataProvider(D dao, BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);

		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.DA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}

		List<Long> annotationIds = dao.findIdsByParams(params);
		annotationIds.removeIf(Objects::isNull);

		if (StringUtils.equals(dataProvider.toString(), "HUMAN")) {
			Map<String, Object> newParams = new HashMap<>();
			newParams.put(EntityFieldConstants.SECONDARY_DATA_PROVIDER, dataProvider.sourceOrganization);
			newParams.put(EntityFieldConstants.DA_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
			List<Long> additionalIds = dao.findIdsByParams(newParams);
			annotationIds.addAll(additionalIds);
		}

		return annotationIds;
	}

}
