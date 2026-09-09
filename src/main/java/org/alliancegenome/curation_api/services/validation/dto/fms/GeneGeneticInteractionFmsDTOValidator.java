package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.InteractionAnnotationsHelper;
import org.alliancegenome.curation_api.services.helpers.InteractionStringHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneGeneticInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject GeneGeneticInteractionService geneGeneticInteractionService;
	@Inject GeneGeneticInteractionDAO geneGeneticInteractionDAO;
	@Inject AlleleService alleleService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject InteractionAnnotationsHelper interactionAnnotationsHelper;
	@Inject CurieMintService curieMintService;

	private Map<String, long[]> existingInteractionMap;
	private ObjectResponse<GeneGeneticInteraction> ggiResponse;

	public void setExistingInteractionMap(Map<String, long[]> map) {
		this.existingInteractionMap = map;
	}

	public ObjectResponse<GeneGeneticInteraction> validateGeneGeneticInteractionFmsDTO(PsiMiTabDTO dto) throws ValidationException {

		GeneGeneticInteraction interaction = null;
		ggiResponse = new ObjectResponse<GeneGeneticInteraction>();

		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		ggiResponse.addErrorMessages(refResponse.getErrorMessages());

		String interactionId = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds())) {
			interactionId = PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractionIds().get(0));
		}

		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			ggiResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorAResponse = findAllianceGene(dto.getInteractorAIdentifier(), dto.getInteractorATaxonId());
			if (interactorAResponse.hasErrors()) {
				ggiResponse.addErrorMessage("interactorAIdentifier", interactorAResponse.errorMessagesString());
			}
			interactorA = interactorAResponse.getEntity();
		}

		Gene interactorB = null;
		if (StringUtils.isBlank(dto.getInteractorBIdentifier())) {
			ggiResponse.addErrorMessage("interactorBIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			ObjectResponse<Gene> interactorBResponse = findAllianceGene(dto.getInteractorBIdentifier(), dto.getInteractorBTaxonId());
			if (interactorBResponse.hasErrors()) {
				ggiResponse.addErrorMessage("interactorBIdentifier", interactorBResponse.errorMessagesString());
			}
			interactorB = interactorBResponse.getEntity();
		}

		List<Reference> references = refResponse.getEntity();

		List<String> phenotypesOrTraits = interactionAnnotationsHelper.extractPhenotypeStatements(dto.getInteractionAnnotations());

		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto, interactorA, interactorB, interactionId, references, phenotypesOrTraits);

		// Fast path: use pre-loaded map to skip unchanged records or find existing by PK
		if (existingInteractionMap != null && interactionId != null) {
			long[] existing = existingInteractionMap.get(interactionId);
			if (existing != null) {
				long existingId = existing[0];
				long existingUniqueIdHash = existing[1];
				if (uniqueId != null && uniqueId.hashCode() == existingUniqueIdHash) {
					interaction = new GeneGeneticInteraction();
					interaction.setId(existingId);
					ggiResponse.setEntity(interaction);
					return ggiResponse;
				}
				interaction = geneGeneticInteractionDAO.find(existingId);
			}
		}
		if (interaction == null) {
			String searchValue = interactionId == null ? uniqueId : interactionId;
			ObjectResponse<GeneGeneticInteraction> interactionResponse = geneGeneticInteractionService.getByIdentifier(searchValue);
			if (interactionResponse != null) {
				interaction = interactionResponse.getEntity();
			}
		}
		if (interaction == null) {
			interaction = new GeneGeneticInteraction();
		}

		interaction.setUniqueId(uniqueId);
		interaction.setGeneAssociationSubject(interactorA);
		interaction.setGeneGeneAssociationObject(interactorB);
		interaction.setInteractionId(interactionId);
		interaction.setPhenotypesOrTraits(handleStringListField(phenotypesOrTraits));

		ObjectResponse<GeneGeneticInteraction> giResponse = validateGeneInteraction(interaction, dto, references);
		ggiResponse.addErrorMessages(giResponse.getErrorMessages());
		interaction = giResponse.getEntity();

		interaction.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_VOCABULARY_TERM_SET, VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM).getEntity());

		Allele interactorAGeneticPerturbation = validatePerturbation("interactorAAnnotationString", dto.getInteractorAAnnotationString());
		interaction.setInteractorAGeneticPerturbation(interactorAGeneticPerturbation);

		Allele interactorBGeneticPerturbation = validatePerturbation("interactorBAnnotationString", dto.getInteractorBAnnotationString());
		interaction.setInteractorBGeneticPerturbation(interactorBGeneticPerturbation);

		if (ggiResponse.hasErrors()) {
			throw new ObjectValidationException(dto, ggiResponse.getErrorMessages().values());
		}
		
		// SCRUM-6463 — mint an AGRKB curie for a new interaction, in the same transaction as the insert
		// below. No is-new guard is needed, unlike AlleleValidator: PsiMiTabDTO carries no curie, so
		// nothing above nulls one, and a changed record resolves to the stored entity whose curie is
		// already set, making this a no-op there.
		//
		// It has to sit here rather than earlier: the unchanged-record fast path above returns a detached
		// stub that is never persisted, so minting before that point would burn a MaTI id for every
		// unchanged row of a reload.
		curieMintService.mintCurieIfAbsent(interaction, MatiSubdomain.GENETIC_INTERACTION);
		ggiResponse.setEntity(geneGeneticInteractionDAO.persist(interaction));

		return ggiResponse;

	}

	private Allele validatePerturbation(String fieldName, String dtoField) {
		Allele geneticPerturbation = null;
		String geneticPerturbationCurie = InteractionStringHelper.extractWBVarCurieFromAnnotations(dtoField);
		if (geneticPerturbationCurie == null) {
			return null;
		}

		geneticPerturbation = alleleService.findByIdentifierString(geneticPerturbationCurie);
		if (geneticPerturbation == null) {
			ggiResponse.addErrorMessage(fieldName, ValidationConstants.INVALID_MESSAGE + " (" + geneticPerturbationCurie + ")");
		}

		return geneticPerturbation;
	}
}
