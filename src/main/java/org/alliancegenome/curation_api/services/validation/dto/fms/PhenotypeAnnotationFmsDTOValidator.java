package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.alliancegenome.curation_api.constants.ReferenceConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConditionRelationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeTermIdentifierFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.DataProviderService;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.ontology.PhenotypeTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class PhenotypeAnnotationFmsDTOValidator {

	@Inject
	ReferenceService referenceService;
	@Inject
	PhenotypeTermService phenotypeTermService;
	@Inject
	ConditionRelationFmsDTOValidator conditionRelationFmsDtoValidator;
	@Inject
	ConditionRelationDAO conditionRelationDAO;
	@Inject
	DataProviderService dataProviderService;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;
	
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
					if (phenotypeTerm != null)
						phenotypeTerms.add(phenotypeTerm);
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
		
		annotation.setDataProvider(dataProviderService.createOrganizationDataProvider(beDataProvider.sourceOrganization));
		annotation.setRelation(vocabularyTermService.getTermInVocabulary(VocabularyConstants.PHENOTYPE_RELATION_VOCABULARY, "has_phenotype").getEntity());
		
		CrossReference evidenceXref = null;
		if (dto.getEvidence() != null && dto.getEvidence().getPublicationId() != null) {
			if (annotation.getCrossReference() != null && Objects.equals(annotation.getCrossReference().getDisplayName(), dto.getEvidence().getPublicationId())) {
				evidenceXref = annotation.getCrossReference();
			} else {
				evidenceXref = createXrefFromPublicationId(dto.getEvidence().getPublicationId());
				if (evidenceXref == null)
					paResponse.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " for generating cross reference (" + dto.getEvidence().getPublicationId() + ")");
			}
		}
		annotation.setCrossReference(evidenceXref);
		
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
		
		paResponse.setEntity(annotation);

		return paResponse;

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
				String refCurie = dto.getEvidence().getPublicationId();
				if (refCurie.startsWith("OMIM:") || refCurie.startsWith("MIM:") || refCurie.startsWith("ORPHA:"))
					refCurie = ReferenceConstants.RGD_OMIM_ORPHANET_REFERENCE;
				reference = referenceService.retrieveFromDbOrLiteratureService(refCurie);
				if (reference == null)
					refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE);
			}
		}
		
		refResponse.setEntity(reference);
		return refResponse;
	}
	
	private CrossReference createXrefFromPublicationId(String curie) {
		CrossReference xref = new CrossReference();
		
		xref.setReferencedCurie(curie);
		xref.setDisplayName(curie);
		
		String[] curieParts = curie.split(":");
		String prefix = curieParts[0];
		
		ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, "default");
		if (page == null)
			return null;
		xref.setResourceDescriptorPage(page);
		
		return xref;
	}

}
