package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
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
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.GeneService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

@RequestScoped
public class AlleleGeneAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator {

	@Inject
	AlleleGeneAssociationDAO alleleGeneAssociationDAO;
	@Inject
	AlleleService alleleService;
	@Inject
	GeneService geneService;
	@Inject
	VocabularyTermService vocabularyTermService;

	public AlleleGeneAssociation validateAlleleGeneAssociationDTO(AlleleGeneAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ObjectValidationException {
		ObjectResponse<AlleleGeneAssociation> agaResponse = new ObjectResponse<>();

		Long subjectId = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			agaResponse.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectId = alleleService.getIdByModID(dto.getAlleleIdentifier());
			if (subjectId == null) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		Long objectId = null;
		if (StringUtils.isBlank(dto.getGeneIdentifier())) {
			agaResponse.addErrorMessage("gene_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectId = geneService.getIdByModID(dto.getGeneIdentifier());
			if (objectId == null) {
				agaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			}
		}

		AlleleGeneAssociation association = null;
		if (subjectId != null && objectId != null && StringUtils.isNotBlank(dto.getRelationName())) {
			HashMap<String, Object> params = new HashMap<>();

			params.put("alleleAssociationSubject.id", subjectId);
			params.put("relation.name", dto.getRelationName());
			params.put("alleleGeneAssociationObject.id", objectId);

			SearchResponse<AlleleGeneAssociation> searchResponse = alleleGeneAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			}
		}

		if (association == null) {
			association = new AlleleGeneAssociation();
		}

		VocabularyTerm relation = null;
		if (StringUtils.isNotEmpty(dto.getRelationName())) {
			relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_GENE_RELATION_VOCABULARY_TERM_SET, dto.getRelationName()).getEntity();
			if (relation == null) {
				agaResponse.addErrorMessage("relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getRelationName() + ")");
			}
		} else {
			agaResponse.addErrorMessage("relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}
		association.setRelation(relation);

		if (association.getAlleleAssociationSubject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

			Allele subject = alleleService.getShallowEntity(alleleService.getIdByModID(dto.getAlleleIdentifier()));
			if (subject == null) {
				agaResponse.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			} else {
				association.setAlleleAssociationSubject(subject);
			}
		}

		if (association.getAlleleGeneAssociationObject() == null && objectId != null) {

			Gene object = geneService.getShallowEntity(geneService.getIdByModID(dto.getGeneIdentifier()));
			if (object == null) {
				agaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getGeneIdentifier() + ")");
			} /*else if (beDataProvider != null && !object.getDataProvider().getSourceOrganization().getAbbreviation().equals(beDataProvider.sourceOrganization)) {
				agaResponse.addErrorMessage("gene_identifier", ValidationConstants.INVALID_MESSAGE + " for " + beDataProvider.name() + " load (" + dto.getGeneIdentifier() + ")");
			} */ else {
				association.setAlleleGeneAssociationObject(object);
			}
		}

		ObjectResponse<AlleleGenomicEntityAssociation> ageaResponse = validateAlleleGenomicEntityAssociationDTO(association, dto);
		agaResponse.addErrorMessages(ageaResponse.getErrorMessages());
		association = (AlleleGeneAssociation) ageaResponse.getEntity();

		if (agaResponse.hasErrors()) {
			throw new ObjectValidationException(dto, agaResponse.errorMessagesString());
		}

	//	association = alleleGeneAssociationDAO.persist(association);
		return association;
	}
}
