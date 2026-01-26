package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.associations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.AgmAlleleAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.GENOTerm;
import org.alliancegenome.curation_api.model.ingest.dto.associations.AgmAlleleAssociationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.AffectedGenomicModelService;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.GenoTermService;
import org.alliancegenome.curation_api.services.validation.dto.base.AuditedObjectDTOValidator;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AgmAlleleAssociationDTOValidator extends AuditedObjectDTOValidator<AgmAlleleAssociation, AgmAlleleAssociationDTO> {
	@Inject
	AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject
	AffectedGenomicModelService agmService;
	@Inject
	AlleleService alleleService;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	GenoTermService genoTermService;

	public ObjectResponse<AgmAlleleAssociation> validateAgmAlleleAssociationDTO(AgmAlleleAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<AgmAlleleAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			response.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = agmService.findIdsByIdentifierString(dto.getAgmSubjectIdentifier());
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			response.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = alleleService.findIdsByIdentifierString(dto.getAlleleIdentifier());
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.AGM_ALLELE_RELATION_VOCABULARY_TERM_SET);

		GENOTerm zygosity = validateOntologyTerm(genoTermService, "zygosity_curie", dto.getZygosityCurie());
		if (zygosity != null) {
			validateTermInVocabulary("zygosity_curie", dto.getZygosityCurie(), VocabularyConstants.AGM_ALLELE_GENOTYPE_TERMS_VOCABULARY);
		}

		AgmAlleleAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			HashMap<String, Object> params = new HashMap<>();

			params.put(EntityFieldConstants.AGM_ASSOCIATION_SUBJECT + ".id", subjectIds.get(0));
			params.put(EntityFieldConstants.RELATION + ".id", relation.getId());
			params.put(EntityFieldConstants.AGM_ALLELE_ASSOCIATION_OBJECT + ".id", objectIds.get(0));

			SearchResponse<AgmAlleleAssociation> searchResponse = agmAlleleAssociationDAO.findByParams(params);
			if (searchResponse != null && searchResponse.getResults().size() == 1) {
				association = searchResponse.getSingleResult();
			} else {
				if (association == null) {
					association = new AgmAlleleAssociation();
				}

				association.setRelation(relation);

				if (association.getAgmAssociationSubject() == null && !StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {

					AffectedGenomicModel subject = agmService.findByIdentifierString(dto.getAgmSubjectIdentifier());
					if (subject == null) {
						response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
					} else if (dataProvider != null && !subject.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
					} else {
						association.setAgmAssociationSubject(subject);
					}
				}

				if (association.getAgmAlleleAssociationObject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

					Allele object = alleleService.findByIdentifierString(dto.getAlleleIdentifier());
					if (object == null) {
						response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
					} else if (dataProvider != null && !object.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getAlleleIdentifier() + ")");
					} else {
						association.setAgmAlleleAssociationObject(object);
					}
				}
			}

			association.setZygosity(zygosity);
			association = validateAuditedObjectDTO(association, dto);
			association = agmAlleleAssociationDAO.persist(association);
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		response.setEntity(association);

		return response;
	}
}
