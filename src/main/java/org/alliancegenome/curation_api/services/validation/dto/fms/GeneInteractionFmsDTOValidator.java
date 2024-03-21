package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionCrossReferenceHelper;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.alliancegenome.curation_api.services.ontology.MiTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionFmsDTOValidator {

	@Inject
	ReferenceService referenceService;
	@Inject
	GeneService geneService;
	@Inject
	MiTermService miTermService;
	@Inject
	CrossReferenceDAO crossReferenceDAO;
	@Inject
	InteractionCrossReferenceHelper interactionXrefHelper;
	
	public <E extends GeneInteraction> ObjectResponse<E> validateGeneInteraction(E interaction, PsiMiTabDTO dto, List<Reference> references) {

		ObjectResponse<E> giResponse = new ObjectResponse<E>();
		
		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			giResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			interactorA = findAllianceGene(dto.getInteractorAIdentifier());
			if (interactorA == null)
				giResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.INVALID_MESSAGE);
		}
		interaction.setGeneAssociationSubject(interactorA);
		
		Gene interactorB = null;
		if (StringUtils.isBlank(dto.getInteractorBIdentifier())) {
			giResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			interactorB = findAllianceGene(dto.getInteractorBIdentifier());
			if (interactorB == null)
				giResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.INVALID_MESSAGE);
		}
		interaction.setGeneGeneAssociationObject(interactorB);
		
		if (CollectionUtils.isNotEmpty(references)) {
			List<InformationContentEntity> evidence = new ArrayList<>();
			evidence.addAll(references);
			interaction.setEvidence(evidence);
		} else {
			interaction.setEvidence(null);
		}
		
		MITerm interactorARole = null;
		if (StringUtils.isNotBlank(dto.getExperimentalRoleA())) {
			interactorARole = miTermService.findByCurie(InteractionStringHelper.extractCurieFromPsiMiFormat(dto.getExperimentalRoleA()));
			if (interactorARole == null)
				giResponse.addErrorMessage("experimentalRoleA", ValidationConstants.INVALID_MESSAGE + " (" + dto.getExperimentalRoleA() + ")");
		}
		interaction.setInteractorARole(interactorARole);
		
		MITerm interactorBRole = null;
		if (StringUtils.isNotBlank(dto.getExperimentalRoleB())) {
			interactorBRole = miTermService.findByCurie(InteractionStringHelper.extractCurieFromPsiMiFormat(dto.getExperimentalRoleB()));
			if (interactorBRole == null)
				giResponse.addErrorMessage("experimentalRoleB", ValidationConstants.INVALID_MESSAGE + " (" + dto.getExperimentalRoleB() + ")");
		}
		interaction.setInteractorBRole(interactorBRole);
		
		MITerm interactorAType = null;
		if (StringUtils.isNotBlank(dto.getInteractorAType())) {
			interactorAType = miTermService.findByCurie(InteractionStringHelper.extractCurieFromPsiMiFormat(dto.getInteractorAType()));
			if (interactorAType == null)
				giResponse.addErrorMessage("interactorAType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInteractorAType() + ")");
		}
		interaction.setInteractorAType(interactorAType);
		
		MITerm interactorBType = null;
		if (StringUtils.isNotBlank(dto.getInteractorBType())) {
			interactorBType = miTermService.findByCurie(InteractionStringHelper.extractCurieFromPsiMiFormat(dto.getInteractorBType()));
			if (interactorBType == null)
				giResponse.addErrorMessage("interactorBType", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInteractorBType() + ")");
		}
		interaction.setInteractorBType(interactorBType);
		
		MITerm interactionType = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionTypes())) {
			for (String interactionTypeString : dto.getInteractionTypes()) {
				String interactionTypeCurie = InteractionStringHelper.extractCurieFromPsiMiFormat(interactionTypeString);
				if (interactionTypeCurie != null) {
					interactionType = miTermService.findByCurie(interactionTypeCurie);
					if (interactionType == null)
						giResponse.addErrorMessage("interactionTypes", ValidationConstants.INVALID_MESSAGE + " (" + interactionTypeCurie + ")");
					break;
				}
			}
		}
		interaction.setInteractionType(interactionType);
		
		MITerm interactionSource = null;
		if (CollectionUtils.isNotEmpty(dto.getSourceDatabaseIds())) {
			for (String interactionSourceString : dto.getSourceDatabaseIds()) {
				String interactionSourceCurie = InteractionStringHelper.extractCurieFromPsiMiFormat(interactionSourceString);
				if (interactionSourceCurie != null) {
					interactionSource = miTermService.findByCurie(interactionSourceCurie);
					if (interactionSource == null)
						giResponse.addErrorMessage("sourceDatabaseIds", ValidationConstants.INVALID_MESSAGE + " (" + interactionSourceCurie + ")");
					break;
				}
			}
		}
		interaction.setInteractionSource(interactionSource);
		
		List<CrossReference> xrefs = updateInteractionXrefs(interaction.getCrossReferences(), dto);
		if (interaction.getCrossReferences() != null)
			interaction.getCrossReferences().clear();
		if (CollectionUtils.isNotEmpty(xrefs)) {
			if (interaction.getCrossReferences() == null)
				interaction.setCrossReferences(new ArrayList<>());
			interaction.getCrossReferences().addAll(xrefs);
		}
		
		OffsetDateTime dateCreated = processPsiMiTabDateFormat(dto.getCreationDate());
		OffsetDateTime dateUpdated = processPsiMiTabDateFormat(dto.getUpdateDate());
		interaction.setDateCreated(dateCreated);
		interaction.setDateUpdated(dateUpdated);
		
		giResponse.setEntity(interaction);

		return giResponse;

	}
	
	private Gene findAllianceGene(String psiMiTabIdentifier) {
		String[] psiMiTabIdParts = psiMiTabIdentifier.split(":");
		if (psiMiTabIdParts.length != 2)
			return null;
		
		PsiMiTabPrefixEnum prefix = PsiMiTabPrefixEnum.findByPsiMiTabPrefix(psiMiTabIdParts[0]);
		if (prefix == null)
			return null;
		
		Gene allianceGene = null;
		if (prefix.isModPrefix) {
			allianceGene = geneService.findByIdentifierString(prefix.alliancePrefix + ":" + psiMiTabIdParts[1]);
		} else {
			// TODO: lookup gene via xref
		}
		
		return allianceGene;
	}
	
	protected ObjectResponse<List<Reference>> validateReferences(PsiMiTabDTO dto) {
		ObjectResponse<List<Reference>> refResponse = new ObjectResponse<>();
		List<Reference> validatedReferences = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(dto.getPublicationIds())) {
			for (String publicationId : dto.getPublicationIds()) {
				Reference reference = null;
				String alliancePubXrefCurie = InteractionStringHelper.getAllianceCurie(publicationId);
				if (alliancePubXrefCurie != null)
					reference = referenceService.retrieveFromDbOrLiteratureService(alliancePubXrefCurie);
				if (reference == null) {
					refResponse.addErrorMessage("publicationIds", ValidationConstants.INVALID_MESSAGE + " (" + publicationId + ")");
					return refResponse;
				}
				validatedReferences.add(reference);
			}
		}
		if (CollectionUtils.isNotEmpty(validatedReferences))
			refResponse.setEntity(validatedReferences);
		
		return refResponse;
	}
	
	private List<CrossReference> updateInteractionXrefs(List<CrossReference> existingXrefs, PsiMiTabDTO dto) {
		List<CrossReference> newXrefs = interactionXrefHelper.createAllianceXrefs(dto);
		if (CollectionUtils.isEmpty(newXrefs))
			return null;
		if (CollectionUtils.isEmpty(existingXrefs))
			return newXrefs;
		
		Map<String, CrossReference> existingXrefMap = existingXrefs.stream()
				.collect(Collectors.toMap(CrossReference::getReferencedCurie, Function.identity()));
		List<CrossReference> updatedXrefs = new ArrayList<>();
		for (CrossReference newXref : newXrefs) {
			if (existingXrefMap.containsKey(newXref.getReferencedCurie())) {
				updatedXrefs.add(existingXrefMap.get(newXref.getReferencedCurie()));
			} else {
				updatedXrefs.add(crossReferenceDAO.persist(newXref));
			}
		}
		
		return updatedXrefs;
	}
	
	private OffsetDateTime processPsiMiTabDateFormat(String dateString) {
		if (StringUtils.isBlank(dateString))
			return null;
		DateTimeFormatter dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		dateString = dateString.replace("/", "-");
		return OffsetDateTime.parse(dateString + "T00:00:00+00:00", dtf);
	}

}
