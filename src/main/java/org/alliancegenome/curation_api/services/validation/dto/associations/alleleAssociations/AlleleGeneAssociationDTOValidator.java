package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import java.util.HashMap;

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

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

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
			
		Allele subject = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			agaResponse.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subject = alleleDAO.findByIdentifierString(dto.getAlleleIdentifier());
			if (subject == null) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getAlleleIdentifier() + ")");
			}
		}
		
		Gene object = null;
		if (StringUtils.isBlank(dto.getGeneIdentifier())) {
			agaResponse.addErrorMessage("gene_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			object = geneDAO.findByIdentifierString(dto.getGeneIdentifier());
			if (object == null) {
				agaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			} else if (beDataProvider != null && !subject.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getGeneIdentifier() + ")");
			}
		}
		
		AlleleGeneAssociation association = null;
		if (subject != null && object != null && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("subject.id", subject.getId());
			params.put("relation.name", dto.getRelationName());
			params.put("object.id", object.getId());
			
			SearchResponse<AlleleGeneAssociation> searchResponse = alleleGeneAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}
		if (association == null)
			association = new AlleleGeneAssociation();
			
		association.setSubject(subject);
		association.setObject(object);
		
		ObjectResponse<AlleleGenomicEntityAssociation> ageaResponse = validateAlleleGenomicEntityAssociationDTO(association, dto);
		agaResponse.addErrorMessages(ageaResponse.getErrorMessages());
		association = (AlleleGeneAssociation) ageaResponse.getEntity();

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
		
		return association; 
	}
}
