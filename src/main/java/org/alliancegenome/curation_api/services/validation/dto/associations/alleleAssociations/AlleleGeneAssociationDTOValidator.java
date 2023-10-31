package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import java.util.HashMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleGeneAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGenomicEntityAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleGeneAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleGeneAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator {

	@Inject
	AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	VocabularyTermService  vocabularyTermService;

	public AlleleGeneAssociation validateAlleleGeneAssociationDTO(AlleleGeneAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		ObjectResponse<AlleleGeneAssociation> agaResponse = new ObjectResponse<AlleleGeneAssociation>();
				
		Long noteIdToDelete = null;
		
		AlleleGeneAssociation association = null;
		if (StringUtils.isNotBlank(dto.getAlleleCurie()) && StringUtils.isNotBlank(dto.getGeneCurie()) && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("subject.curie", dto.getAlleleCurie());
			params.put("relation.name", dto.getRelationName());
			params.put("object.curie", dto.getGeneCurie());
			
			SearchResponse<AlleleGeneAssociation> searchResponse = alleleGeneAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
				if (association.getRelatedNote() != null)
					noteIdToDelete = association.getRelatedNote().getId();
			}
		}
		if (association == null)
			association = new AlleleGeneAssociation();
			
		ObjectResponse<AlleleGenomicEntityAssociation> ageaResponse = validateAlleleGenomicEntityAssociationDTO(association, dto);
		agaResponse.addErrorMessages(ageaResponse.getErrorMessages());
		association = (AlleleGeneAssociation) ageaResponse.getEntity();

		if (StringUtils.isBlank(dto.getAlleleCurie())) {
			agaResponse.addErrorMessage("allele_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			Allele allele = alleleDAO.find(dto.getAlleleCurie());
			if (allele == null) {
				agaResponse.addErrorMessage("allele_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleCurie() + ")");
			} else if (beDataProvider != null && !allele.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("allele_curie", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load");
			}
			association.setSubject(allele);	
		}
		
		if (StringUtils.isBlank(dto.getGeneCurie())) {
			agaResponse.addErrorMessage("gene_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			Gene gene = geneDAO.find(dto.getGeneCurie());
			if (gene == null)
				agaResponse.addErrorMessage("gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneCurie() + ")");
			association.setObject(gene);
		}
		
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (relation == null)
				agaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			association.setRelation(relation);
		} else {
			agaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (agaResponse.hasErrors())
			throw new ObjectValidationException(dto, agaResponse.errorMessagesString());
			
		association = alleleGeneAssociationDAO.persist(association);
		
		if (noteIdToDelete != null)
			noteDAO.remove(noteIdToDelete);
		
		return association; 
	}
}
