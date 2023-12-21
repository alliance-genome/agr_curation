package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class AGMDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject
	AffectedGenomicModelDAO affectedGenomicModelDAO;

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;

	@Inject
	VocabularyTermService vocabularyTermService;

	@Inject
	AlleleDAO alleleDAO;

	private String errorMessage;

	public AGMDiseaseAnnotation validateAnnotationUpdate(AGMDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update AGM Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No AGM Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		AGMDiseaseAnnotation dbEntity = agmDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find AGM Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been
			// found
		}

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AGMDiseaseAnnotation validateAnnotationCreate(AGMDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create AGM Disease Annotation";

		AGMDiseaseAnnotation dbEntity = new AGMDiseaseAnnotation();

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AGMDiseaseAnnotation validateAnnotation(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {

		AffectedGenomicModel subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setSubject(subject);

		Gene inferredGene = validateInferredGene(uiEntity, dbEntity);
		dbEntity.setInferredGene(inferredGene);

		List<Gene> assertedGenes = validateAssertedGenes(uiEntity, dbEntity);
		dbEntity.setAssertedGenes(assertedGenes);

		Allele inferredAllele = validateInferredAllele(uiEntity, dbEntity);
		dbEntity.setInferredAllele(inferredAllele);

		Allele assertedAllele = validateAssertedAllele(uiEntity, dbEntity);
		dbEntity.setAssertedAllele(assertedAllele);

		VocabularyTerm relation = validateDiseaseRelation(uiEntity, dbEntity);
		dbEntity.setRelation(relation);

		dbEntity = (AGMDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			Log.info("ERROR: " + response.getErrorMessages());
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private AffectedGenomicModel validateSubject(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getSubject()) || uiEntity.getSubject().getId() == null) {
			addMessageResponse("subject", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		AffectedGenomicModel subjectEntity = affectedGenomicModelDAO.find(uiEntity.getSubject().getId());
		if (subjectEntity == null) {
			addMessageResponse("subject", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getSubject() == null || !subjectEntity.getId().equals(dbEntity.getSubject().getId()))) {
			addMessageResponse("subject", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return subjectEntity;

	}

	private Gene validateInferredGene(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (uiEntity.getInferredGene() == null)
			return null;

		Gene inferredGene = geneDAO.find(uiEntity.getInferredGene().getId());
		if (inferredGene == null) {
			addMessageResponse("inferredGene", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inferredGene.getObsolete() && (dbEntity.getInferredGene() == null || !inferredGene.getId().equals(dbEntity.getInferredGene().getId()))) {
			addMessageResponse("inferredGene", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return inferredGene;
	}

	private List<Gene> validateAssertedGenes(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getAssertedGenes()))
			return null;

		List<Gene> assertedGenes = new ArrayList<Gene>();
		List<Long> previousIds = new ArrayList<Long>();
		if (CollectionUtils.isNotEmpty(dbEntity.getAssertedGenes()))
			previousIds = dbEntity.getAssertedGenes().stream().map(Gene::getId).collect(Collectors.toList());
		for (Gene gene : uiEntity.getAssertedGenes()) {
			Gene assertedGene = geneDAO.find(gene.getId());
			if (assertedGene == null) {
				addMessageResponse("assertedGenes", ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (assertedGene.getObsolete() && !previousIds.contains(assertedGene.getId())) {
				addMessageResponse("assertedGenes", ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			assertedGenes.add(assertedGene);
		}

		return assertedGenes;
	}

	private Allele validateInferredAllele(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (uiEntity.getInferredAllele() == null)
			return null;

		Allele inferredAllele = alleleDAO.find(uiEntity.getInferredAllele().getId());
		if (inferredAllele == null) {
			addMessageResponse("inferredAllele", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inferredAllele.getObsolete() && (dbEntity.getInferredAllele() == null || !inferredAllele.getId().equals(dbEntity.getInferredAllele().getId()))) {
			addMessageResponse("inferredAllele", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return inferredAllele;
	}

	private Allele validateAssertedAllele(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		if (uiEntity.getAssertedAllele() == null)
			return null;

		Allele assertedAllele = alleleDAO.find(uiEntity.getAssertedAllele().getId());
		if (assertedAllele == null) {
			addMessageResponse("assertedAllele", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (assertedAllele.getObsolete() && (dbEntity.getAssertedAllele() == null || !assertedAllele.getId().equals(dbEntity.getAssertedAllele().getId()))) {
			addMessageResponse("assertedAllele", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return assertedAllele;
	}

	private VocabularyTerm validateDiseaseRelation(AGMDiseaseAnnotation uiEntity, AGMDiseaseAnnotation dbEntity) {
		String field = "relation";
		if (uiEntity.getRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation().getName()).getEntity();

		if (relation == null) {
			addMessageResponse(field, ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (relation.getObsolete() && (dbEntity.getRelation() == null || !relation.getName().equals(dbEntity.getRelation().getName()))) {
			addMessageResponse(field, ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return relation;
	}
}
