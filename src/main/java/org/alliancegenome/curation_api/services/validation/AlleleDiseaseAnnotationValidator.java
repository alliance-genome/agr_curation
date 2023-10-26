package org.alliancegenome.curation_api.services.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AlleleDiseaseAnnotationValidator extends DiseaseAnnotationValidator {

	@Inject
	AlleleDAO alleleDAO;

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;

	@Inject
	VocabularyTermService vocabularyTermService;

	private String errorMessage;

	public AlleleDiseaseAnnotation validateAnnotationUpdate(AlleleDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Could not update Gene Disease Annotation: [" + uiEntity.getId() + "]";

		Long id = uiEntity.getId();
		if (id == null) {
			addMessageResponse("No Gene Disease Annotation ID provided");
			throw new ApiErrorException(response);
		}
		AlleleDiseaseAnnotation dbEntity = alleleDiseaseAnnotationDAO.find(id);
		if (dbEntity == null) {
			addMessageResponse("Could not find Gene Disease Annotation with ID: [" + id + "]");
			throw new ApiErrorException(response);
			// do not continue validation for update if Disease Annotation ID has not been
			// found
		}

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AlleleDiseaseAnnotation validateAnnotationCreate(AlleleDiseaseAnnotation uiEntity) {
		response = new ObjectResponse<>(uiEntity);
		errorMessage = "Cound not create Allele Disease Annotation";

		AlleleDiseaseAnnotation dbEntity = new AlleleDiseaseAnnotation();

		return validateAnnotation(uiEntity, dbEntity);
	}

	public AlleleDiseaseAnnotation validateAnnotation(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {

		Allele subject = validateSubject(uiEntity, dbEntity);
		dbEntity.setSubject(subject);

		Gene inferredGene = validateInferredGene(uiEntity, dbEntity);
		dbEntity.setInferredGene(inferredGene);

		List<Gene> assertedGenes = validateAssertedGenes(uiEntity, dbEntity);
		dbEntity.setAssertedGenes(assertedGenes);

		VocabularyTerm relation = validateDiseaseRelation(uiEntity, dbEntity);
		dbEntity.setRelation(relation);

		dbEntity = (AlleleDiseaseAnnotation) validateCommonDiseaseAnnotationFields(uiEntity, dbEntity);

		if (response.hasErrors()) {
			response.setErrorMessage(errorMessage);
			throw new ApiErrorException(response);
		}

		return dbEntity;
	}

	private Allele validateSubject(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		if (ObjectUtils.isEmpty(uiEntity.getSubject()) || StringUtils.isBlank(uiEntity.getSubject().getCurie())) {
			addMessageResponse("subject", ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		Allele subjectEntity = alleleDAO.find(uiEntity.getSubject().getCurie());
		if (subjectEntity == null) {
			addMessageResponse("subject", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (subjectEntity.getObsolete() && (dbEntity.getSubject() == null || !subjectEntity.getCurie().equals(dbEntity.getSubject().getCurie()))) {
			addMessageResponse("subject", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}
		return subjectEntity;

	}

	private Gene validateInferredGene(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		if (uiEntity.getInferredGene() == null)
			return null;

		Gene inferredGene = geneDAO.find(uiEntity.getInferredGene().getCurie());
		if (inferredGene == null) {
			addMessageResponse("inferredGene", ValidationConstants.INVALID_MESSAGE);
			return null;
		}

		if (inferredGene.getObsolete() && (dbEntity.getInferredGene() == null || !inferredGene.getCurie().equals(dbEntity.getInferredGene().getCurie()))) {
			addMessageResponse("inferredGene", ValidationConstants.OBSOLETE_MESSAGE);
			return null;
		}

		return inferredGene;
	}

	private List<Gene> validateAssertedGenes(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		if (CollectionUtils.isEmpty(uiEntity.getAssertedGenes()))
			return null;

		List<Gene> assertedGenes = new ArrayList<Gene>();
		List<String> previousCuries = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(dbEntity.getAssertedGenes()))
			previousCuries = dbEntity.getAssertedGenes().stream().map(Gene::getCurie).collect(Collectors.toList());
		for (Gene gene : uiEntity.getAssertedGenes()) {
			Gene assertedGene = geneDAO.find(gene.getCurie());
			if (assertedGene == null) {
				addMessageResponse("assertedGenes", ValidationConstants.INVALID_MESSAGE);
				return null;
			}
			if (assertedGene.getObsolete() && !previousCuries.contains(assertedGene.getCurie())) {
				addMessageResponse("assertedGenes", ValidationConstants.OBSOLETE_MESSAGE);
				return null;
			}
			assertedGenes.add(assertedGene);
		}

		return assertedGenes;
	}

	private VocabularyTerm validateDiseaseRelation(AlleleDiseaseAnnotation uiEntity, AlleleDiseaseAnnotation dbEntity) {
		String field = "relation";
		if (uiEntity.getRelation() == null) {
			addMessageResponse(field, ValidationConstants.REQUIRED_MESSAGE);
			return null;
		}

		VocabularyTerm relation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, uiEntity.getRelation().getName()).getEntity();

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
