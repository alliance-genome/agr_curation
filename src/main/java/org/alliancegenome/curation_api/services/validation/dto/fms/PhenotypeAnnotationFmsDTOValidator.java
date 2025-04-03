package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.ExternalDatabaseReferenceDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.ExternalDatabaseReference;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConditionRelationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeTermIdentifierFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.InformationContentEntityService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class PhenotypeAnnotationFmsDTOValidator {

	@Inject InformationContentEntityService iceService;
	@Inject PhenotypeTermService phenotypeTermService;
	@Inject ConditionRelationFmsDTOValidator conditionRelationFmsDtoValidator;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject OrganizationService organizationService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject ExternalDatabaseReferenceDAO externalDatabaseReferenceDAO;

	public <E extends PhenotypeAnnotation> ObjectResponse<E> validatePhenotypeAnnotation(E annotation, PhenotypeFmsDTO dto, BackendBulkDataProvider beDataProvider) {

		ObjectResponse<E> paResponse = new ObjectResponse<E>();

		if (StringUtils.isBlank(dto.getPhenotypeStatement())) {
			paResponse.addErrorMessage("phenotypeStatement", ValidationConstants.REQUIRED_MESSAGE);
		}
		annotation.setPhenotypeAnnotationObject(dto.getPhenotypeStatement());

		if (CollectionUtils.isNotEmpty(dto.getPhenotypeTermIdentifiers())) {
			List<PhenotypeTerm> phenotypeTerms = new ArrayList<>();
			for (PhenotypeTermIdentifierFmsDTO phenotypeTermIdentifier : dto.getPhenotypeTermIdentifiers()) {
				if (StringUtils.isNotBlank(phenotypeTermIdentifier.getTermId())) {
					PhenotypeTerm phenotypeTerm = phenotypeTermService.findByCurieOrSecondaryId(phenotypeTermIdentifier.getTermId());
					if (phenotypeTerm != null) {
						phenotypeTerms.add(phenotypeTerm);
					}
				}
			}
			annotation.setPhenotypeTerms(phenotypeTerms);
		} else {
			annotation.setPhenotypeTerms(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getConditionRelations())) {
			List<ConditionRelation> relations = new ArrayList<>();
			for (ConditionRelationFmsDTO conditionRelationFmsDTO : dto.getConditionRelations()) {
				ObjectResponse<ConditionRelation> crResponse = conditionRelationFmsDtoValidator.validateConditionRelationFmsDTO(conditionRelationFmsDTO);
				if (crResponse.hasErrors()) {
					paResponse.addErrorMessage("conditionRelations", crResponse.errorMessagesString());
				} else {
					relations.add(conditionRelationDAO.persist(crResponse.getEntity()));
				}
			}
			annotation.setConditionRelations(relations);
		} else {
			annotation.setConditionRelations(null);
		}

		annotation.setDataProvider(organizationService.getByAbbr(beDataProvider.sourceOrganization).getEntity());
		annotation.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.PHENOTYPE_RELATION_VOCABULARY, "has_phenotype").getEntity());

		OffsetDateTime creationDate = null;
		if (StringUtils.isNotBlank(dto.getDateAssigned())) {
			try {
				creationDate = OffsetDateTime.parse(dto.getDateAssigned());
			} catch (DateTimeParseException e) {
				paResponse.addErrorMessage("dateAssigned", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDateAssigned() + ")");
			}
		} else {
			paResponse.addErrorMessage("dateAssigned", ValidationConstants.REQUIRED_MESSAGE);
		}
		annotation.setDateCreated(creationDate);

		annotation.setObsolete(false);
		annotation.setInternal(false);

		paResponse.setEntity(annotation);

		return paResponse;

	}

	@Transactional
	public ObjectResponse<InformationContentEntity> validateReference(PhenotypeFmsDTO dto) {
		ObjectResponse<InformationContentEntity> refResponse = new ObjectResponse<>();
		InformationContentEntity reference = null;

		if (dto.getEvidence() == null) {
			refResponse.addErrorMessage("evidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			if (StringUtils.isBlank(dto.getEvidence().getPublicationId())) {
				refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE);
			} else {
				String refCurie = dto.getEvidence().getPublicationId();
				if (refCurie.startsWith("OMIM:")) {
					refCurie = refCurie.substring(1);
				}
				reference = iceService.retrieveFromDbOrLiteratureService(refCurie);
				
				if (reference == null) {
					if (refCurie.startsWith("MIM:") || refCurie.startsWith("ORPHA:")) {
						ExternalDatabaseReference externalDbRef = new ExternalDatabaseReference();
						externalDbRef.setCurie(refCurie);
						reference = externalDatabaseReferenceDAO.persist(externalDbRef);
					} else {
						refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + refCurie + ")");
					}
				}
			}
		}

		refResponse.setEntity(reference);
		return refResponse;
	}

	protected <D extends BaseSQLDAO<E>, E extends PhenotypeAnnotation> List<E> findPrimaryAnnotations(D dao, PhenotypeFmsDTO dto, String primaryAnnotationSubjectprimaryExternalId, String refString) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("phenotypeAnnotationSubject.primaryExternalId", primaryAnnotationSubjectprimaryExternalId);
		if (StringUtils.isNotBlank(dto.getPhenotypeStatement())) {
			params.put("phenotypeAnnotationObject", dto.getPhenotypeStatement());
		} else {
			return null;
		}
		if (StringUtils.isNotBlank(refString)) {
			params.put("evidenceItem.curie", refString);
		} else {
			return null;
		}
		
		SearchResponse<E> searchResponse = dao.findByParams(params);
		if (searchResponse == null || CollectionUtils.isEmpty(searchResponse.getResults())) {
			return null;
		}
		
		String secondaryPhenotypeTermIdString = "";
		if (CollectionUtils.isNotEmpty(dto.getPhenotypeTermIdentifiers())) {
			List<String> validPhenotypeTermCuries = new ArrayList<>();
			for (PhenotypeTermIdentifierFmsDTO phenotypeTermIdentifier : dto.getPhenotypeTermIdentifiers()) {
				if (StringUtils.isNotBlank(phenotypeTermIdentifier.getTermId())) {
					PhenotypeTerm phenotypeTerm = phenotypeTermService.findByCurieOrSecondaryId(phenotypeTermIdentifier.getTermId());
					if (phenotypeTerm != null) {
						validPhenotypeTermCuries.add(phenotypeTerm.getCurie());
					}
				}
			}
			if (CollectionUtils.isNotEmpty(validPhenotypeTermCuries)) {
				secondaryPhenotypeTermIdString = getPhenotypeTermIdString(validPhenotypeTermCuries);
			}
		}
		
		List<E> primaryAnnotations = new ArrayList<>();
		for (E possiblePrimaryAnnotation : searchResponse.getResults()) {
			String primaryPhenotypeTermIdString = "";
			if (CollectionUtils.isNotEmpty(possiblePrimaryAnnotation.getPhenotypeTerms())) {
				primaryPhenotypeTermIdString = getPhenotypeTermIdString(possiblePrimaryAnnotation.getPhenotypeTerms().stream().map(PhenotypeTerm::getCurie).collect(Collectors.toList()));
			}
			if (primaryPhenotypeTermIdString.equals(secondaryPhenotypeTermIdString)) {
				primaryAnnotations.add(possiblePrimaryAnnotation);
			}
		}
		if (CollectionUtils.isEmpty(primaryAnnotations)) {
			return null;
		}
		
		return primaryAnnotations;
	}
	
	private String getPhenotypeTermIdString(List<String> identifiers) {
		if (CollectionUtils.isNotEmpty(identifiers)) {
			Collections.sort(identifiers);
		} else {
			return "";
		}
		
		return String.join("|", identifiers);
	}

}
