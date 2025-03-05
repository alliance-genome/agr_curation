package org.alliancegenome.curation_api.services.validation.dto.associations.alleleAssociations;

import java.util.Objects;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.alleleAssociations.AlleleConstructAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations.AlleleConstructAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.ConstructService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AlleleConstructAssociationDTOValidator extends AlleleGenomicEntityAssociationDTOValidator<AlleleConstructAssociation, AlleleConstructAssociationDTO> {

	@Inject AlleleConstructAssociationDAO alleleConstructAssociationDAO;
	@Inject AlleleService alleleService;
	@Inject ConstructService constructService;
	public AlleleConstructAssociation validateAlleleConstructAssociationDTO(AlleleConstructAssociationDTO dto, BackendBulkDataProvider beDataProvider) throws ValidationException {
		
		response = new ObjectResponse<AlleleConstructAssociation>();

		Allele subject = validateRequiredIdentifier(alleleService, "allele_identifier", dto.getAlleleIdentifier());
		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET);
		
		AlleleConstructAssociation association = new AlleleConstructAssociation();
		if (subject != null && relation != null && StringUtils.isNotBlank(dto.getConstructIdentifier())) {
			if (CollectionUtils.isNotEmpty(subject.getAlleleConstructAssociations())) {
				for (AlleleConstructAssociation existingAssociation : subject.getAlleleConstructAssociations()) {
					if (existingAssociation.getRelation().getId().equals(relation.getId())) {
						if (Objects.equals(existingAssociation.getAlleleConstructAssociationObject().getPrimaryExternalId(), dto.getConstructIdentifier())
								|| Objects.equals(existingAssociation.getAlleleConstructAssociationObject().getModInternalId(), dto.getConstructIdentifier())) {
							association = existingAssociation;
							break;
						}
					}
				}
			}
		}
		association.setAlleleAssociationSubject(subject);
		association.setRelation(relation);
		
		if (association.getAlleleConstructAssociationObject() == null) {
			Construct object = validateRequiredIdentifier(constructService, "construct_identifier", dto.getConstructIdentifier());
			association.setAlleleConstructAssociationObject(object);
		}

		association = validateAlleleGenomicEntityAssociationDTO(association, dto, VocabularyConstants.ALLELE_CONSTRUCT_RELATION_VOCABULARY_TERM_SET);
		
		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}
		
		return alleleConstructAssociationDAO.persist(association);
	}
}
