package org.alliancegenome.curation_api.services.validation.dto.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private HashMap<String, List<Long>> agmIdCache = new HashMap<>();
	private HashMap<String, List<Long>> alleleIdCache = new HashMap<>();
	private HashMap<String, AffectedGenomicModel> agmCache = new HashMap<>();
	private HashMap<String, Allele> alleleCache = new HashMap<>();
	private Map<String, Long> existingIdentityKeys;
	private Map<String, Long> existingFullStateKeys;

	public void preloadAssociationKeys(Map<String, Long> identityKeys, Map<String, Long> fullStateKeys) {
		this.existingIdentityKeys = identityKeys;
		this.existingFullStateKeys = fullStateKeys;
	}

	public ObjectResponse<AgmAlleleAssociation> validateAgmAlleleAssociationDTO(AgmAlleleAssociationDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		response = new ObjectResponse<AgmAlleleAssociation>();

		List<Long> subjectIds = null;
		if (StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {
			response.addErrorMessage("agm_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			subjectIds = agmIdCache.computeIfAbsent(dto.getAgmSubjectIdentifier(), agmService::findIdsByIdentifierString);
			if (subjectIds == null || subjectIds.size() != 1) {
				response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
			}
		}

		List<Long> objectIds = null;
		if (StringUtils.isBlank(dto.getAlleleIdentifier())) {
			response.addErrorMessage("allele_identifier", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			objectIds = alleleIdCache.computeIfAbsent(dto.getAlleleIdentifier(), alleleService::findIdsByIdentifierString);
			if (objectIds == null || objectIds.size() != 1) {
				response.addErrorMessage("allele_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleIdentifier() + ")");
			}
		}

		VocabularyTerm relation = validateRequiredTermInVocabularyTermSet("relation_name", dto.getRelationName(), VocabularyConstants.AGM_ALLELE_RELATION_VOCABULARY_TERM_SET);

		GENOTerm zygosity = validateOntologyTerm(genoTermService, "zygosity_curie", dto.getZygosityCurie());
		if (zygosity != null) {
			validateTermInVocabulary("zygosity_curie", dto.getZygosityCurie(), VocabularyConstants.AGM_ALLELE_GENOTYPE_TERMS_VOCABULARY);
		}

		if (response.hasErrors()) {
			throw new ObjectValidationException(dto, response.errorMessagesString());
		}

		AgmAlleleAssociation association = null;
		if (subjectIds != null && subjectIds.size() == 1 && objectIds != null && objectIds.size() == 1 && relation != null) {
			String identityKey = subjectIds.get(0) + "|" + relation.getId() + "|" + objectIds.get(0);

			Boolean internal = dto.getInternal() != null ? dto.getInternal() : false;
			Boolean obsolete = dto.getObsolete() != null ? dto.getObsolete() : false;
			String fullKey = identityKey + "|" + (zygosity != null ? zygosity.getId() : "null") + "|" + internal + "|" + obsolete;

			// Check if record is completely unchanged -- skip all DB work
			if (existingFullStateKeys != null && existingFullStateKeys.containsKey(fullKey)) {
				Long existingId = existingFullStateKeys.get(fullKey);
				association = new AgmAlleleAssociation();
				association.setId(existingId);
				response.setEntity(association);
				return response;
			}

			// Record exists but changed -- find and update
			if (existingIdentityKeys != null && existingIdentityKeys.containsKey(identityKey)) {
				association = agmAlleleAssociationDAO.find(existingIdentityKeys.get(identityKey));
			} else {
				association = agmAlleleAssociationDAO.findBySubjectAndRelationAndObject(subjectIds.get(0), relation.getId(), objectIds.get(0));
			}

			if (association == null) {
				association = new AgmAlleleAssociation();

				association.setRelation(relation);

				if (association.getAgmAssociationSubject() == null && !StringUtils.isBlank(dto.getAgmSubjectIdentifier())) {

					AffectedGenomicModel subject = agmCache.computeIfAbsent(dto.getAgmSubjectIdentifier(), agmService::findByIdentifierString);
					if (subject == null) {
						response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmSubjectIdentifier() + ")");
					} else if (dataProvider != null && !subject.getDataProvider().getAbbreviation().equals(dataProvider.sourceOrganization)) {
						response.addErrorMessage("agm_identifier", ValidationConstants.INVALID_MESSAGE + " for " + dataProvider.name() + " load (" + dto.getAgmSubjectIdentifier() + ")");
					} else {
						association.setAgmAssociationSubject(subject);
					}
				}

				if (association.getAgmAlleleAssociationObject() == null && !StringUtils.isBlank(dto.getAlleleIdentifier())) {

					Allele object = alleleCache.computeIfAbsent(dto.getAlleleIdentifier(), alleleService::findByIdentifierString);
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
