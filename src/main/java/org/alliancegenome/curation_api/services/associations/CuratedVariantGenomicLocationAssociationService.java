package org.alliancegenome.curation_api.services.associations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.CuratedVariantGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.VariantFmsDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class CuratedVariantGenomicLocationAssociationService extends BaseEntityCrudService<CuratedVariantGenomicLocationAssociation, CuratedVariantGenomicLocationAssociationDAO> {

	@Inject CuratedVariantGenomicLocationAssociationDAO curatedVariantGenomicLocationAssociationDAO;
	@Inject VariantFmsDTOValidator variantFmsDtoValidator;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(curatedVariantGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = curatedVariantGenomicLocationAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CuratedVariantGenomicLocationAssociation> getLocationAssociation(Long exonId, Long assemblyComponentId) {
		CuratedVariantGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT + ".id", exonId);
		params.put(EntityFieldConstants.VARIANT_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".id", assemblyComponentId);

		SearchResponse<CuratedVariantGenomicLocationAssociation> resp = curatedVariantGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CuratedVariantGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}
	
	public void addAssociationToSubject(CuratedVariantGenomicLocationAssociation association) {
		Variant variant = association.getVariantAssociationSubject();
		
		List<CuratedVariantGenomicLocationAssociation> currentSubjectAssociations = variant.getCuratedVariantGenomicLocations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}
		
		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(CuratedVariantGenomicLocationAssociation::getId).collect(Collectors.toList());
		
		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}
}
