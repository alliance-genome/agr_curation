package org.alliancegenome.curation_api.services.validation.dto.fms;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ReferenceConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.ConditionRelationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.ontology.PhenotypeTerm;
import org.alliancegenome.curation_api.services.helpers.GenePhenotypeAnnotationXrefHelper;
import org.alliancegenome.curation_api.services.helpers.annotations.AnnotationUniqueIdHelper;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConditionRelationFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.CrossReferenceFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeFmsDTO;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PhenotypeTermIdentifierFmsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.OrganizationService;
import org.alliancegenome.curation_api.services.ReferenceService;
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

	@Inject ReferenceService referenceService;
	@Inject PhenotypeTermService phenotypeTermService;
	@Inject ConditionRelationFmsDTOValidator conditionRelationFmsDtoValidator;
	@Inject ConditionRelationDAO conditionRelationDAO;
	@Inject OrganizationService organizationService;
	@Inject VocabularyTermService vocabularyTermService;
	@Inject ResourceDescriptorPageService resourceDescriptorPageService;
	@Inject CrossReferenceService crossReferenceService;
	@Inject GenePhenotypeAnnotationXrefHelper xrefHelper;
	@Inject GeneDAO geneDAO;

	protected Map<String, Long> existingUniqueIds;
	protected Map<String, Long> inferredGeneIds;
	protected Map<String, Long> inferredAlleleIds;
	protected Set<Long> geneIdsWithPhenotypeXref;

	public void setExistingUniqueIds(Map<String, Long> existingUniqueIds) {
		this.existingUniqueIds = existingUniqueIds;
	}

	public void setInferredGeneIds(Map<String, Long> inferredGeneIds) {
		this.inferredGeneIds = inferredGeneIds;
	}

	public void setInferredAlleleIds(Map<String, Long> inferredAlleleIds) {
		this.inferredAlleleIds = inferredAlleleIds;
	}

	public void setGeneIdsWithPhenotypeXref(Set<Long> geneIdsWithPhenotypeXref) {
		this.geneIdsWithPhenotypeXref = geneIdsWithPhenotypeXref;
	}

	/**
	 * Ensures the gene carries the Alliance-derived gene/phenotypes cross reference that the gene page
	 * links to. Annotation loads skip records that are unchanged, so without this the cross reference is
	 * only ever written for new or changed annotations and never restored for the rest. The preloaded
	 * set holds the genes already dealt with, keeping this to one attempt per gene per load.
	 */
	protected void ensureGenePhenotypeCrossReference(BackendBulkDataProvider dataProvider, Gene gene) {
		if (gene == null || gene.getId() == null) {
			return;
		}
		if (geneIdsWithPhenotypeXref != null && geneIdsWithPhenotypeXref.contains(gene.getId())) {
			return;
		}
		xrefHelper.addGenePhenotypeCrossReference(dataProvider, gene);
		if (geneIdsWithPhenotypeXref != null) {
			geneIdsWithPhenotypeXref.add(gene.getId());
		}
	}

	protected void ensureGenePhenotypeCrossReference(BackendBulkDataProvider dataProvider, Long geneId) {
		if (geneId == null) {
			return;
		}
		if (geneIdsWithPhenotypeXref != null && geneIdsWithPhenotypeXref.contains(geneId)) {
			return;
		}
		ensureGenePhenotypeCrossReference(dataProvider, geneDAO.find(geneId));
		if (geneIdsWithPhenotypeXref != null) {
			// Marked even when the id is not a gene, or the page does not exist for its resource
			// descriptor, so that it is only looked up once per load rather than once per annotation
			geneIdsWithPhenotypeXref.add(geneId);
		}
	}

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
	public ObjectResponse<Reference> validateReference(PhenotypeFmsDTO dto) {
		ObjectResponse<Reference> refResponse = new ObjectResponse<>();
		Reference reference = null;

		if (dto.getEvidence() == null) {
			refResponse.addErrorMessage("evidence", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			if (StringUtils.isBlank(dto.getEvidence().getPublicationId())) {
				refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.REQUIRED_MESSAGE);
			} else {
				String refCurie = dto.getEvidence().getPublicationId();
				if (getOmimOrphanetCurie(dto) != null) {
					refCurie = ReferenceConstants.RGD_OMIM_ORPHANET_REFERENCE;
				}
				reference = referenceService.retrieveFromDbOrLiteratureService(refCurie);
				if (reference == null) {
					refResponse.addErrorMessage("evidence - publicationId", ValidationConstants.INVALID_MESSAGE + " (" + refCurie + ")");
				}
			}
		}

		refResponse.setEntity(reference);
		return refResponse;
	}

	/**
	 * Returns the MIM or ORPHA curie of an evidence publicationId (OMIM: is read as MIM:), or null
	 * for any other publicationId. These records cite the generic RGD OMIM/Orphanet reference and
	 * carry the curie itself in the annotation's crossReference and uniqueId.
	 */
	public String getOmimOrphanetCurie(PhenotypeFmsDTO dto) {
		if (dto.getEvidence() == null || StringUtils.isBlank(dto.getEvidence().getPublicationId())) {
			return null;
		}
		String curie = dto.getEvidence().getPublicationId();
		if (curie.startsWith("OMIM:")) {
			curie = curie.substring(1);
		}
		if (curie.startsWith("MIM:") || curie.startsWith("ORPHA:")) {
			return curie;
		}
		return null;
	}

	protected String getRefString(PhenotypeFmsDTO dto, Reference reference) {
		String omimOrphanetCurie = getOmimOrphanetCurie(dto);
		if (omimOrphanetCurie != null) {
			return omimOrphanetCurie;
		}
		return reference == null ? null : reference.getCurie();
	}

	protected void setOmimOrphanetCrossReference(PhenotypeAnnotation annotation, PhenotypeFmsDTO dto) {
		String curie = getOmimOrphanetCurie(dto);
		if (curie == null) {
			return;
		}

		String prefix = curie.split(":")[0];
		ResourceDescriptorPage page = null;
		CrossReferenceFmsDTO xrefDto = dto.getEvidence().getCrossReference();
		if (xrefDto != null && CollectionUtils.isNotEmpty(xrefDto.getPages())) {
			page = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, xrefDto.getPages().get(0));
		}
		if (page == null) {
			page = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, "default");
		}

		CrossReference xref = new CrossReference();
		xref.setReferencedCurie(curie);
		xref.setDisplayName(curie);
		xref.setResourceDescriptorPage(page);

		// Keep an unchanged cross reference so orphanRemoval does not delete and re-insert it
		if (!Objects.equals(crossReferenceService.getCrossReferenceUniqueId(annotation.getCrossReference()), crossReferenceService.getCrossReferenceUniqueId(xref))) {
			annotation.setCrossReference(xref);
		}
	}

	protected <D extends BaseSQLDAO<E>, E extends PhenotypeAnnotation> List<E> findPrimaryAnnotations(D dao, PhenotypeFmsDTO dto, String primaryAnnotationSubjectIdentifier, String refString) {
		// Fast path: compute uniqueId and look up in pre-loaded map
		if (existingUniqueIds != null) {
			String uniqueId = AnnotationUniqueIdHelper.getPhenotypeAnnotationUniqueId(dto, primaryAnnotationSubjectIdentifier, refString);
			Long existingId = existingUniqueIds.get(uniqueId);
			if (existingId != null) {
				E annotation = dao.find(existingId);
				if (annotation != null) {
					return List.of(annotation);
				}
			}
			return null;
		}

		// Fallback: original findByParams path
		HashMap<String, Object> params = new HashMap<>();
		params.put("phenotypeAnnotationSubject.primaryExternalId", primaryAnnotationSubjectIdentifier);
		if (StringUtils.isNotBlank(dto.getPhenotypeStatement())) {
			params.put("phenotypeAnnotationObject", dto.getPhenotypeStatement());
		} else {
			return null;
		}
		if (StringUtils.isNotBlank(refString)) {
			if (getOmimOrphanetCurie(dto) != null) {
				params.put("crossReference.referencedCurie", refString);
			} else {
				params.put("evidenceItem.curie", refString);
			}
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
