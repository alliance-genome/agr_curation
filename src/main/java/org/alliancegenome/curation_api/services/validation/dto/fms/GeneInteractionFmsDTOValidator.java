package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionFmsDTOValidator {

	@Inject
	ReferenceService referenceService;
	@Inject
	GeneService geneService;
	
	public <E extends GeneInteraction> ObjectResponse<E> validateGeneInteraction(E interaction, PsiMiTabDTO dto) {

		ObjectResponse<E> giResponse = new ObjectResponse<E>();
		
		Gene interactorA = null;
		if (StringUtils.isBlank(dto.getInteractorAIdentifier())) {
			giResponse.addErrorMessage("interactorAIdentifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			interactorA = findAllianceGene(dto.getInteractorAIdentifier());
		}
		
		
		giResponse.setEntity(interaction);

		return giResponse;

	}
	
	public ObjectResponse<Reference> validateReference(PhenotypeFmsDTO dto) {
		ObjectResponse<Reference> refResponse = new ObjectResponse<>();
		Reference reference = null;
		
		if (ObjectUtils.isEmpty(dto.getEvidence())) {
			refResponse.addErrorMessage("evidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			if (StringUtils.isBlank(dto.getEvidence().getPublicationId())) {
				refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE);
			} else {
				reference = referenceService.retrieveFromDbOrLiteratureService(dto.getEvidence().getPublicationId());
				if (reference == null)
					refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE);
			}
		}
		
		refResponse.setEntity(reference);
		return refResponse;
	}
	
	public Gene findAllianceGene(String psiMiTabIdentifier) {
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

}
