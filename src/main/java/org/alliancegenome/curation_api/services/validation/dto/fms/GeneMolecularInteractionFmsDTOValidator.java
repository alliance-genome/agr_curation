package org.alliancegenome.curation_api.services.validation.dto.fms;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.AGMPhenotypeAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.GenomicEntityService;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneMolecularInteractionFmsDTOValidator extends GeneInteractionFmsDTOValidator {

	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject
	GenomicEntityService genomicEntityService;
	
	public GeneMolecularInteraction validateGeneMolecularInteractionFmsDTO(PsiMiTabDTO dto) throws ObjectValidationException {

		ObjectResponse<GeneMolecularInteraction> gmiResponse = new ObjectResponse<GeneMolecularInteraction>();
		GeneMolecularInteraction interaction = new GeneMolecularInteraction();
		
		ObjectResponse<Reference> refResponse = validateReference(dto);
		gmiResponse.addErrorMessages(refResponse.getErrorMessages());
			
		Reference reference = refResponse.getEntity();
		String refString = reference == null ? null : reference.getCurie();
		
		String uniqueId = InteractionUniqueIdHelper.getInteractionUniqueId(dto, subject.getIdentifier(), refString);
		SearchResponse<AGMPhenotypeAnnotation> annotationSearch = agmPhenotypeAnnotationDAO.findByField("uniqueId", uniqueId);
		if (annotationSearch != null && annotationSearch.getSingleResult() != null)
			annotation = annotationSearch.getSingleResult();

		annotation.setUniqueId(uniqueId);
		annotation.setSingleReference(reference);
		annotation.setPhenotypeAnnotationSubject(subject);
		
		// Reset implied/asserted fields as secondary annotations loaded separately
		annotation.setAssertedAllele(null);
		annotation.setAssertedGenes(null);
		annotation.setInferredAllele(null);
		annotation.setInferredGene(null);
		
		ObjectResponse<AGMPhenotypeAnnotation> paResponse = validatePhenotypeAnnotation(annotation, dto, dataProvider);
		apaResponse.addErrorMessages(paResponse.getErrorMessages());
		annotation = paResponse.getEntity();
		
		if (apaResponse.hasErrors())
			throw new ObjectValidationException(dto, gmiResponse.errorMessagesString());
		
		return interaction;

	}
	
	public ObjectResponse<Reference> validateReference(PsiMiTabDTO dto) {
		ObjectResponse<Reference> refResponse = new ObjectResponse<>();
		Reference reference = null;
		
		//TODO: work this out for PSI-MI-TAB input
		refResponse.setEntity(reference);
		return refResponse;
	}
}
