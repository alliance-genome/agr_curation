package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AGMDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationUniqueIdHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class AGMDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	VocabularyTermService vocabularyTermService;
	@Inject
	AffectedGenomicModelDAO agmDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	AlleleDAO alleleDAO;

	public AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException, ObjectValidationException {

		AGMDiseaseAnnotation annotation = new AGMDiseaseAnnotation();
		AffectedGenomicModel agm;

		ObjectResponse<AGMDiseaseAnnotation> adaResponse = new ObjectResponse<AGMDiseaseAnnotation>();

		ObjectResponse<AGMDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		adaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getAgmCurie())) {
			adaResponse.addErrorMessage("agm_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			agm = agmDAO.find(dto.getAgmCurie());
			if (agm == null) {
				adaResponse.addErrorMessage("agm_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAgmCurie() + ")");
			} else {
				String annotationId;
				String identifyingField;
				String uniqueId = DiseaseAnnotationUniqueIdHelper.getDiseaseAnnotationUniqueId(dto, dto.getAgmCurie(), refCurie);
				
				if (StringUtils.isNotBlank(dto.getModEntityId())) {
					annotationId = dto.getModEntityId();
					annotation.setModEntityId(annotationId);
					identifyingField = "modEntityId";
				} else if (StringUtils.isNotBlank(dto.getModInternalId())) {
					annotationId = dto.getModInternalId();
					annotation.setModInternalId(annotationId);
					identifyingField = "modInternalId";
				} else {
					annotationId = uniqueId;
					identifyingField = "uniqueId";
				}

				SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField(identifyingField, annotationId);
				if (annotationList != null && annotationList.getResults().size() > 0) {
					annotation = annotationList.getResults().get(0);
				}
				annotation.setUniqueId(uniqueId);
				annotation.setSubject(agm);
			}
		}
		annotation.setSingleReference(validatedReference);

		ObjectResponse<AGMDiseaseAnnotation> daResponse = validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermService.getTermInVocabularyTermSet(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName()).getEntity();
			if (diseaseRelation == null)
				adaResponse.addErrorMessage("disease_relation_name", ValidationConstants.INVALID_MESSAGE + " (" + dto.getDiseaseRelationName() + ")");
			annotation.setDiseaseRelation(diseaseRelation);
		} else {
			adaResponse.addErrorMessage("disease_relation_name", ValidationConstants.REQUIRED_MESSAGE);
		}

		if (StringUtils.isNotBlank(dto.getInferredGeneCurie())) {
			Gene inferredGene = geneDAO.find(dto.getInferredGeneCurie());
			if (inferredGene == null)
				adaResponse.addErrorMessage("inferred_gene_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInferredGeneCurie() + ")");
			annotation.setInferredGene(inferredGene);
		} else {
			annotation.setInferredGene(null);
		}

		if (CollectionUtils.isNotEmpty(dto.getAssertedGeneCuries())) {
			List<Gene> assertedGenes = new ArrayList<>();
			for (String assertedGeneCurie : dto.getAssertedGeneCuries()) {
				Gene assertedGene = geneDAO.find(assertedGeneCurie);
				if (assertedGene == null) {
					adaResponse.addErrorMessage("asserted_gene_curies", ValidationConstants.INVALID_MESSAGE + " (" + assertedGeneCurie + ")");
				} else {
					assertedGenes.add(assertedGene);
				}
			}
			annotation.setAssertedGenes(assertedGenes);
		} else {
			annotation.setAssertedGenes(null);
		}

		if (StringUtils.isNotBlank(dto.getInferredAlleleCurie())) {
			Allele inferredAllele = alleleDAO.find(dto.getInferredAlleleCurie());
			if (inferredAllele == null)
				adaResponse.addErrorMessage("inferred_allele_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getInferredAlleleCurie() + ")");
			annotation.setInferredAllele(inferredAllele);
		} else {
			annotation.setInferredAllele(null);
		}

		if (StringUtils.isNotBlank(dto.getAssertedAlleleCurie())) {
			Allele assertedAllele = alleleDAO.find(dto.getAssertedAlleleCurie());
			if (assertedAllele == null)
				adaResponse.addErrorMessage("asserted_allele_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAssertedAlleleCurie() + ")");
			annotation.setAssertedAllele(assertedAllele);
		} else {
			annotation.setAssertedAllele(null);
		}

		if (adaResponse.hasErrors())
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		
		return annotation;
	}
}
