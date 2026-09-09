package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.InteractionStringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneMolecularInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject GeneMolecularInteractionService geneMolecularInteractionService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject CurieMintService curieMintService;

	private Map<String, long[]> existingInteractionMap;
	private ObjectResponse<GeneMolecularInteraction> gmiResponse;

	public void setExistingInteractionMap(Map<String, long[]> map) {
		this.existingInteractionMap = map;
	}

	public ObjectResponse<GeneMolecularInteraction> validateGeneMolecularInteractionFmsDTO(PsiMiTabDTO dto) throws ValidationException {

		GeneMolecularInteraction interaction = null;
		gmiResponse = new ObjectResponse<GeneMolecularInteraction>();

		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		gmiResponse.addErrorMessages(refResponse.getErrorMessages());

		String interactionId = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds())) {
			interactionId = PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractionIds().get(0));
		}

		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			gmiResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorAResponse = findAllianceGene(dto.getInteractorAIdentifier(), dto.getInteractorATaxonId());
			if (interactorAResponse.hasErrors()) {
				gmiResponse.addErrorMessage("interactorAIdentifier", interactorAResponse.errorMessagesString());
			}
			interactorA = interactorAResponse.getEntity();
		}

		Gene interactorB = null;
		if (StringUtils.isBlank(dto.getInteractorBIdentifier())) {
			gmiResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorBResponse = findAllianceGene(dto.getInteractorBIdentifier(), dto.getInteractorBTaxonId());
			if (interactorBResponse.hasErrors()) {
				gmiResponse.addErrorMessage("interactorBIdentifier", interactorBResponse.errorMessagesString());
			}
			interactorB = interactorBResponse.getEntity();
		}

		List<Reference> references = refResponse.getEntity();

		String uniqueId = InteractionStringHelper.getGeneMolecularInteractionUniqueId(dto, interactorA, interactorB, interactionId, references);

		// Fast path: use pre-loaded map to skip unchanged records or find existing by PK
		if (existingInteractionMap != null && interactionId != null) {
			long[] existing = existingInteractionMap.get(interactionId);
			if (existing != null) {
				long existingId = existing[0];
				long existingUniqueIdHash = existing[1];
				// Skip entirely if uniqueId hasn't changed
				if (uniqueId != null && uniqueId.hashCode() == existingUniqueIdHash) {
					interaction = new GeneMolecularInteraction();
					interaction.setId(existingId);
					gmiResponse.setEntity(interaction);
					return gmiResponse;
				}
				// Changed -- load by PK instead of findByParams
				interaction = geneMolecularInteractionDAO.find(existingId);
			}
		}
		if (interaction == null) {
			String searchValue = interactionId == null ? uniqueId : interactionId;
			ObjectResponse<GeneMolecularInteraction> interactionResponse = geneMolecularInteractionService.getByIdentifier(searchValue);
			if (interactionResponse != null) {
				interaction = interactionResponse.getEntity();
			}
		}
		if (interaction == null) {
			interaction = new GeneMolecularInteraction();
		}

		interaction.setUniqueId(uniqueId);
		interaction.setGeneAssociationSubject(interactorA);
		interaction.setGeneGeneAssociationObject(interactorB);
		interaction.setInteractionId(interactionId);

		ObjectResponse<GeneMolecularInteraction> giResponse = validateGeneInteraction(interaction, dto, references);
		gmiResponse.addErrorMessages(giResponse.getErrorMessages());
		interaction = giResponse.getEntity();

		interaction.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_VOCABULARY_TERM_SET, VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM).getEntity());

		MITerm detectionMethod = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionDetectionMethods())) {
			for (String detectionMethodString : dto.getInteractionDetectionMethods()) {
				String detectionMethodCurie = getCurieFromCache(detectionMethodString);
				if (detectionMethodCurie != null) {
					detectionMethod = getTermFromCache(detectionMethodCurie);
					if (detectionMethod == null) {
						gmiResponse.addErrorMessage("interactionDetectionMethods", ValidationConstants.INVALID_MESSAGE + " (" + detectionMethodCurie + ")");
					}
					break;
				}
			}
		}
		interaction.setDetectionMethod(detectionMethod);

		MITerm aggregationDatabase = null;
		String aggregationDatabaseCurie = InteractionStringHelper.getAggregationDatabaseMITermCurie(dto);
		if (aggregationDatabaseCurie != null) {
			aggregationDatabase = getTermFromCache(aggregationDatabaseCurie);
			if (aggregationDatabase == null) {
				gmiResponse.addErrorMessage("aggregationDatabase (inferred from sourceDatabaseIds)", ValidationConstants.INVALID_MESSAGE + " (" + aggregationDatabaseCurie + ")");
			}
		}
		interaction.setAggregationDatabase(aggregationDatabase);

		if (gmiResponse.hasErrors()) {
			throw new ObjectValidationException(dto, gmiResponse.errorMessagesList());
		}
		
		// SCRUM-6463 — mint an AGRKB curie for a new interaction, in the same transaction as the insert
		// below. No is-new guard is needed, unlike AlleleValidator: PsiMiTabDTO carries no curie, so
		// nothing above nulls one, and a changed record resolves to the stored entity whose curie is
		// already set, making this a no-op there.
		//
		// It has to sit here rather than earlier: the unchanged-record fast path above returns a detached
		// stub that is never persisted, so minting before that point would burn a MaTI id for every
		// unchanged row of a reload.
		curieMintService.mintCurieIfAbsent(interaction, MatiSubdomain.MOLECULAR_INTERACTION);
		gmiResponse.setEntity(geneMolecularInteractionDAO.persist(interaction));

		return gmiResponse;

	}
}
