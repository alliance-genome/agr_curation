package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AGMDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.AffectedGenomicModelDAO;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
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
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AGMDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {
	
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject VocabularyTermDAO vocabularyTermDAO;
	@Inject AffectedGenomicModelDAO agmDAO;
	@Inject GeneDAO geneDAO;
	@Inject AlleleDAO alleleDAO;

	public AGMDiseaseAnnotation validateAGMDiseaseAnnotationDTO(AGMDiseaseAnnotationDTO dto) throws ObjectUpdateException, ObjectValidationException {
		
		AGMDiseaseAnnotation annotation = new AGMDiseaseAnnotation();
		AffectedGenomicModel agm;
		
		ObjectResponse<AGMDiseaseAnnotation> adaResponse = new ObjectResponse<AGMDiseaseAnnotation>();
		
		ObjectResponse<AGMDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		adaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();
		
		if (StringUtils.isBlank(dto.getSubject())) {
			adaResponse.addErrorMessage("subject", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			agm = agmDAO.find(dto.getSubject());
			if (agm == null) {
				adaResponse.addErrorMessage("subject", ValidationConstants.INVALID_MESSAGE);
			} else {
				String annotationId = dto.getModEntityId();
				if (StringUtils.isBlank(annotationId)) {
					annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(agm.getTaxon().getCurie()).getCurieID(dto, refCurie);
				}
		
				SearchResponse<AGMDiseaseAnnotation> annotationList = agmDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
				if (annotationList == null || annotationList.getResults().size() == 0) {
					annotation.setUniqueId(annotationId);
					annotation.setSubject(agm);
				} else {
					annotation = annotationList.getResults().get(0);
				}
			}
		}
		annotation.setSingleReference(validatedReference);
		
		ObjectResponse<AGMDiseaseAnnotation> daResponse = validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelation())) {
			VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabulary(VocabularyConstants.AGM_DISEASE_RELATION_VOCABULARY, dto.getDiseaseRelation());
			if (diseaseRelation == null)
				adaResponse.addErrorMessage("diseaseRelation", ValidationConstants.INVALID_MESSAGE);
			annotation.setDiseaseRelation(diseaseRelation);
		} else {
			adaResponse.addErrorMessage("diseaseRelation", ValidationConstants.REQUIRED_MESSAGE);
		}
		
		if (StringUtils.isNotBlank(dto.getInferredGene())) {
			Gene inferredGene = geneDAO.find(dto.getInferredGene());
			if (inferredGene == null)
				adaResponse.addErrorMessage("inferredGene", ValidationConstants.INVALID_MESSAGE);
			annotation.setInferredGene(inferredGene);
		} else {
			annotation.setInferredGene(null);
		}
		
		if (CollectionUtils.isNotEmpty(dto.getAssertedGenes())) {
			List<Gene> assertedGenes = new ArrayList<>();
			for (String assertedGeneCurie : dto.getAssertedGenes()) {
				Gene assertedGene = geneDAO.find(assertedGeneCurie);
				if (assertedGene == null) {
					adaResponse.addErrorMessage("assertedGenes", ValidationConstants.INVALID_MESSAGE);
					break;
				}
				assertedGenes.add(assertedGene);
			}
			annotation.setAssertedGenes(assertedGenes);
		} else {
			annotation.setAssertedGenes(null);
		}
		
		if (StringUtils.isNotBlank(dto.getInferredAllele())) {
			Allele inferredAllele = alleleDAO.find(dto.getInferredAllele());
			if (inferredAllele == null)
				adaResponse.addErrorMessage("inferredAllele", ValidationConstants.INVALID_MESSAGE);
			annotation.setInferredAllele(inferredAllele);
		} else {
			annotation.setInferredAllele(null);
		}
		
		if (StringUtils.isNotBlank(dto.getAssertedAllele())) {
			Allele assertedAllele = alleleDAO.find(dto.getAssertedAllele());
			if (assertedAllele == null)
				adaResponse.addErrorMessage("assertedAllele", ValidationConstants.INVALID_MESSAGE);
			annotation.setAssertedAllele(assertedAllele);
		} else {
			annotation.setAssertedAllele(null);
		}
		
		if (adaResponse.hasErrors())
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		
		return annotation;
	}
}
