package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.ExternalDataBaseEntityDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.HTPIdFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ExternalDataBaseEntityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class ExternalDataBaseEntityFmsDTOValidator {

	@Inject ExternalDataBaseEntityDAO externalDataBaseEntityDAO;
	@Inject ExternalDataBaseEntityService externalDataBaseEntityService;
	@Inject CrossReferenceDAO crossReferenceDAO;
	@Inject CrossReferenceService crossReferenceService;

	@Transactional
	public ExternalDataBaseEntity validateExternalDataBaseEntityFmsDTO(HTPIdFmsDTO dto) throws ObjectValidationException,ObjectUpdateException {

		ObjectResponse<ExternalDataBaseEntity> externalDBEntityResponse = new ObjectResponse<>();
		ExternalDataBaseEntity externalDBEntity = null;
		
		if(StringUtils.isEmpty(dto.getPrimaryId())) {
			externalDBEntityResponse.addErrorMessage("primaryId", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			externalDBEntity = externalDataBaseEntityService.findByCurie(dto.getPrimaryId());
		}

		if(externalDBEntity == null) {
			externalDBEntity = new ExternalDataBaseEntity();
			externalDBEntity.setCurie(dto.getPrimaryId());
		}

		if(CollectionUtils.isNotEmpty(dto.getAlternateIds())) {
			if(externalDBEntity.getSecondaryIdentifiers() == null) {
				externalDBEntity.setSecondaryIdentifiers(new ArrayList<>());
			}
			Set<String> existingSecondaryIds = new HashSet<>(externalDBEntity.getSecondaryIdentifiers());
			Set<String> incomingAlternateIds = new HashSet<>(dto.getAlternateIds());

			existingSecondaryIds.retainAll(incomingAlternateIds);
			existingSecondaryIds.addAll(incomingAlternateIds);
			externalDBEntity.setSecondaryIdentifiers(new ArrayList<>(existingSecondaryIds));
		}

		List<Long> currentXrefIds;
		if (externalDBEntity.getCrossReferences() == null) {
			currentXrefIds = new ArrayList<>();
		} else {
			currentXrefIds = externalDBEntity.getCrossReferences().stream().map(CrossReference::getId).collect(Collectors.toList());
		}

		List<Long> mergedXrefIds;
		if(CollectionUtils.isNotEmpty(dto.getCrossReferences())) {
			List<CrossReference> mergedCrossReferences = crossReferenceService.getMergedFmsXrefList(dto.getCrossReferences(), externalDBEntity.getCrossReferences());
			mergedXrefIds = mergedCrossReferences.stream().map(CrossReference::getId).collect(Collectors.toList());
			externalDBEntity.setCrossReferences(mergedCrossReferences);
		} else {
			mergedXrefIds = new ArrayList<>();
			externalDBEntity.setCrossReferences(null);
		}

		// CrossReference xref = new CrossReference();
		// if(dto.getPreferredCrossReference() != null) {
		// 	if(dto.getPreferredCrossReference().getPages().size() == 1) {
		// 		List<CrossReferenceFmsDTO> incomingXrefDto = new ArrayList<>();
		// 		incomingXrefDto.add(dto.getPreferredCrossReference());
		// 		List<CrossReference> existingXReferences = new ArrayList<>();
		// 		existingXReferences.add(externalDBEntity.getPreferredCrossReference());
		// 		List<CrossReference> preferredXReferences = crossReferenceService.getMergedFmsXrefList(incomingXrefDto, existingXReferences);
		// 		xref = preferredXReferences.get(0);
		// 		externalDBEntity.setPreferredCrossReference(xref);
		// 	} else {
		// 		externalDBEntityResponse.addErrorMessage("preferredCrossReference.pages", ValidationConstants.INVALID_MESSAGE + " Only one page is allowed");
		// 	}
		// }

		externalDataBaseEntityDAO.persist(externalDBEntity);

		for (Long currentId : currentXrefIds) {
			if (!mergedXrefIds.contains(currentId)) {
				System.out.println("Removing CrossReference with ID: " + crossReferenceDAO.find(currentId).getId());
				crossReferenceDAO.remove(currentId);
			}
		}

		if(externalDBEntityResponse.hasErrors()) {
			throw new ObjectValidationException(dto, externalDBEntityResponse.errorMessagesString());
		}
		return externalDBEntity;
	}
}
