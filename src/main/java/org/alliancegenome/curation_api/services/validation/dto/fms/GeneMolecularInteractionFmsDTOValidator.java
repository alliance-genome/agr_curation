package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.MITerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.interactions.InteractionHelper;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneMolecularInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject
	GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject
	GenomicEntityService genomicEntityService;
	@Inject
	VocabularyTermService vocabularyTermService;
	
	private ObjectResponse<GeneMolecularInteraction> gmiResponse;
	
	public GeneMolecularInteraction validateGeneMolecularInteractionFmsDTO(PsiMiTabDTO dto) throws ObjectValidationException {

		gmiResponse = new ObjectResponse<GeneMolecularInteraction>();
		GeneMolecularInteraction interaction = new GeneMolecularInteraction();
		
		ObjectResponse<List<Reference>> refResponse = validateReferences(dto);
		gmiResponse.addErrorMessages(refResponse.getErrorMessages());
	
		List<Reference> references = refResponse.getEntity();
		String uniqueId = InteractionHelper.getGeneMolecularInteractionUniqueId(dto, references);
		
		SearchResponse<GeneMolecularInteraction> interactionSearch = geneMolecularInteractionDAO.findByField("uniqueId", uniqueId);
		if (interactionSearch != null && interactionSearch.getSingleResult() != null)
			interaction = interactionSearch.getSingleResult();

		interaction.setUniqueId(uniqueId);
		
		ObjectResponse<GeneMolecularInteraction> giResponse = validateGeneInteraction(interaction, dto, references);
		gmiResponse.addErrorMessages(giResponse.getErrorMessages());
		interaction = giResponse.getEntity();
		
		interaction.setRelation(vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_VOCABULARY_TERM_SET, VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM).getEntity());
		
		MITerm detectionMethod = null;
		if (CollectionUtils.isNotEmpty(dto.getInteractionDetectionMethods())) {
			for (String detectionMethodString : dto.getInteractionDetectionMethods()) {
				String detectionMethodCurie = InteractionHelper.extractCurieFromPsiMiFormat(detectionMethodString);
				if (detectionMethodCurie != null) {
					detectionMethod = miTermService.findByCurie(detectionMethodCurie);
					if (detectionMethod == null)
						gmiResponse.addErrorMessage("interactionDetectionMethods", ValidationConstants.INVALID_MESSAGE + " (" + detectionMethodCurie + ")");
					break;
				}
			}
		}
		interaction.setDetectionMethod(detectionMethod);
		
		MITerm aggregationDatabase = null;
		String aggregationDatabaseCurie = InteractionHelper.getAggregationDatabaseMITermCurie(dto);
		if (aggregationDatabaseCurie != null) {
			aggregationDatabase = miTermService.findByCurie(aggregationDatabaseCurie);
			if (aggregationDatabase == null)
				gmiResponse.addErrorMessage("aggregationDatabase (inferred from sourceDatabaseIds)", ValidationConstants.INVALID_MESSAGE + " (" + aggregationDatabaseCurie + ")");
		}
		interaction.setAggregationDatabase(aggregationDatabase);		
		
		if (gmiResponse.hasErrors())
			throw new ObjectValidationException(dto, gmiResponse.errorMessagesString());
		
		return interaction;

	}
}
