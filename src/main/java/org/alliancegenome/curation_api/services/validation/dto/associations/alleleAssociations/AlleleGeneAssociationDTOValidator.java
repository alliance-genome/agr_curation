package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleGeneAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator {

	@Inject
	GeneDAO geneDAO;
	@Inject
	VocabularyTermService  vocabularyTermService;

	public AlleleGenomicEntityAssociation validateAlleleGeneAssociationDTO(AlleleGeneAssociation association, AlleleGeneAssociationDTO dto) throws ObjectValidationException {
		ObjectResponse<AlleleGenomicEntityAssociation> assocResponse = validateAlleleGenomicEntityAssociationDTO(association, dto);
		association = (AlleleGeneAssociation) assocResponse.getEntity();

		if (StringUtils.isBlank(dto.getGeneCurie())) {
			assocResponse.addErrorMessage("gene_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			Gene gene = geneDAO.find(dto.getGeneCurie());
			if (gene == null) {
				assocResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneCurie() + ")");
			}
		}
		
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			VocabularyTerm relation = vocabularyTermService.getTermInVocabulary(VocabularyConstants.ALLELE_RELATION_VOCABULARY, dto.getRelationName()).getEntity();
			if (relation == null)
				assocResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			association.setRelation(relation);
		} else {
			assocResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (assocResponse.hasErrors())
			throw new ObjectValidationException(dto, assocResponse.errorMessagesString());
		
		return assocResponse.getEntity();
	}
}
