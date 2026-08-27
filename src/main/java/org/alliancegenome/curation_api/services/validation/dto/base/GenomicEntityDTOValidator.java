package org.alliancegenome.curation_api.services.validation.dto.base;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.CrossReferenceConstants;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GenomicEntity;
import org.alliancegenome.curation_api.model.ingest.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.dto.GenomicEntityDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.validation.dto.CrossReferenceDTOValidator;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;

public class GenomicEntityDTOValidator<E extends GenomicEntity, D extends GenomicEntityDTO> extends BiologicalEntityDTOValidator<E, D> {

	@Inject CrossReferenceDTOValidator crossReferenceDtoValidator;
	@Inject CrossReferenceService crossReferenceService;

	public E validateGenomicEntityDTO(E entity, D dto, BackendBulkDataProvider dataProvider, String noteTypeVocabularyTermSet) {

		entity = validateBiologicalEntityDTO(entity, dto, dataProvider, noteTypeVocabularyTermSet);
		
		List<CrossReference> validatedXrefs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(dto.getCrossReferenceDtos())) {
			for (CrossReferenceDTO xrefDto : dto.getCrossReferenceDtos()) {
				ObjectResponse<CrossReference> xrefResponse = crossReferenceDtoValidator.validateCrossReferenceDTO(xrefDto, null);
				if (xrefResponse.hasErrors()) {
					response.addErrorMessage("cross_reference_dtos", xrefResponse.errorMessagesString());
					break;
				} else {
					validatedXrefs.add(xrefResponse.getEntity());
				}
			}
		}

		validatedXrefs.addAll(getAllianceDerivedXrefs(entity.getCrossReferences()));

		List<CrossReference> xrefs = crossReferenceService.getUpdatedXrefList(validatedXrefs, entity.getCrossReferences());

		if (entity.getCrossReferences() != null) {
			entity.getCrossReferences().clear();
		}
		if (xrefs != null) {
			if (entity.getCrossReferences() == null) {
				entity.setCrossReferences(new ArrayList<>());
			}
			entity.getCrossReferences().addAll(xrefs);
		}
		
		return entity;
	}

	/**
	 * Returns the existing cross references that the Alliance generates itself during annotation loads
	 * rather than reading from the submission file. Submitted cross reference lists are authoritative,
	 * so without carrying these forward an entity load deletes them (orphanRemoval) and the following
	 * annotation load only restores them for records that changed.
	 */
	private List<CrossReference> getAllianceDerivedXrefs(List<CrossReference> existingXrefs) {
		List<CrossReference> derivedXrefs = new ArrayList<>();
		if (CollectionUtils.isEmpty(existingXrefs)) {
			return derivedXrefs;
		}
		for (CrossReference existingXref : existingXrefs) {
			if (existingXref.getResourceDescriptorPage() != null
					&& CrossReferenceConstants.ALLIANCE_DERIVED_PAGE_AREAS.contains(existingXref.getResourceDescriptorPage().getName())) {
				derivedXrefs.add(existingXref);
			}
		}
		return derivedXrefs;
	}

}
