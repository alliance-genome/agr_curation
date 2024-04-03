package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionAnnotationsHelper;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionStringHelper;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneGeneticInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject
	GeneGeneticInteractionService geneGeneticInteractionService;
	@Inject
	AlleleService alleleService;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	InteractionAnnotationsHelper interactionAnnotationsHelper;
	
	private ObjectResponse<GeneGeneticInteraction> ggiResponse;
	
	public GeneGeneticInteraction validateGeneGeneticInteractionFmsDTO(PsiMiTabDTO dto) throws ObjectValidationException {

		// TODO: remove check once loading interactions where interactors are referenced by xref
		if (!InteractionStringHelper.isAllianceInteractor(dto.getInteractorAIdentifier()) || !InteractionStringHelper.isAllianceInteractor(dto.getInteractorBIdentifier()))
			throw new ObjectValidationException(dto, "Loading interactions via interactor xrefs is not yet implemented");
		
		GeneGeneticInteraction interaction = null;
		ggiResponse = new ObjectResponse<GeneGeneticInteraction>();
		
		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		ggiResponse.addErrorMessages(refResponse.getErrorMessages());
	
		String interactionId = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionIds()))
			interactionId = InteractionStringHelper.getAllianceCurie(dto.getInteractionIds().get(0));
		
		List<Reference> references = refResponse.getEntity();
		
		List<String> phenotypesOrTraits = interactionAnnotationsHelper.extractPhenotypeStatements(dto.getInteractionAnnotations());
		
		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto, interactionId, references, phenotypesOrTraits);
		
		String searchValue = interactionId == null ? uniqueId : interactionId;
		ObjectResponse<GeneGeneticInteraction> interactionResponse = geneGeneticInteractionService.getByIdentifier(searchValue);
		if (interactionResponse != null)
			interaction = interactionResponse.getEntity();
		if (interaction == null)
			interaction = new GeneGeneticInteraction();
		
		interaction.setUniqueId(uniqueId);
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
		
		if (ggiResponse.hasErrors())
			throw new ObjectValidationException(dto, ggiResponse.errorMessagesString());

		return interaction;

	}
	
	private Allele validatePerturbation (String fieldName, String dtoField) {
		Allele geneticPerturbation = null;
		String geneticPerturbationCurie = InteractionStringHelper.extractWBVarCurieFromAnnotations(dtoField);
		if (geneticPerturbationCurie == null)
			return null;
		
		geneticPerturbation = alleleService.findByIdentifierString(geneticPerturbationCurie);
		if (geneticPerturbation == null)
			ggiResponse.addErrorMessage(fieldName, ValidationConstants.INVALID_MESSAGE + " (" + geneticPerturbationCurie + ")");

		return geneticPerturbation;
	}
}
