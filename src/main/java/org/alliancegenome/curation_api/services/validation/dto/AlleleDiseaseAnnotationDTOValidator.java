package org.alliancegenome.curation_api.services.validation.dto;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.AlleleDiseaseAnnotationDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.dao.VocabularyTermDAO;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.AlleleDiseaseAnnotationDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.diseaseAnnotations.DiseaseAnnotationCurieManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class AlleleDiseaseAnnotationDTOValidator extends DiseaseAnnotationDTOValidator {

	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AlleleDAO alleleDAO;
	@Inject
	GeneDAO geneDAO;
	@Inject
	VocabularyTermDAO vocabularyTermDAO;

	public AlleleDiseaseAnnotation validateAlleleDiseaseAnnotationDTO(AlleleDiseaseAnnotationDTO dto) throws ObjectValidationException {
		AlleleDiseaseAnnotation annotation = new AlleleDiseaseAnnotation();
		Allele allele;

		ObjectResponse<AlleleDiseaseAnnotation> adaResponse = new ObjectResponse<AlleleDiseaseAnnotation>();

		ObjectResponse<AlleleDiseaseAnnotation> refResponse = validateReference(annotation, dto);
		adaResponse.addErrorMessages(refResponse.getErrorMessages());
		Reference validatedReference = refResponse.getEntity().getSingleReference();
		String refCurie = validatedReference == null ? null : validatedReference.getCurie();

		if (StringUtils.isBlank(dto.getAlleleCurie())) {
			adaResponse.addErrorMessage("allele_curie", ValidationConstants.REQUIRED_MESSAGE);
		} else {
			allele = alleleDAO.find(dto.getAlleleCurie());
			if (allele == null) {
				adaResponse.addErrorMessage("allele_curie", ValidationConstants.INVALID_MESSAGE + " (" + dto.getAlleleCurie() + ")");
			} else {
				String annotationId = dto.getModEntityId();
				if (StringUtils.isBlank(annotationId)) {
					annotationId = DiseaseAnnotationCurieManager.getDiseaseAnnotationCurie(allele.getTaxon().getCurie()).getCurieID(dto, dto.getAlleleCurie(), refCurie);
				}

				SearchResponse<AlleleDiseaseAnnotation> annotationList = alleleDiseaseAnnotationDAO.findByField("uniqueId", annotationId);
				if (annotationList == null || annotationList.getResults().size() == 0) {
					annotation.setUniqueId(annotationId);
				} else {
					annotation = annotationList.getResults().get(0);
				}
				annotation.setSubject(allele);
			}
		}
		annotation.setSingleReference(validatedReference);

		ObjectResponse<AlleleDiseaseAnnotation> daResponse = validateAnnotationDTO(annotation, dto);
		annotation = daResponse.getEntity();
		adaResponse.addErrorMessages(daResponse.getErrorMessages());

		if (StringUtils.isNotEmpty(dto.getDiseaseRelationName())) {
			VocabularyTerm diseaseRelation = vocabularyTermDAO.getTermInVocabularyTermSet(VocabularyConstants.ALLELE_DISEASE_RELATION_VOCABULARY_TERM_SET, dto.getDiseaseRelationName());
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

		if (adaResponse.hasErrors())
			throw new ObjectValidationException(dto, adaResponse.errorMessagesString());
		
		return annotation;
	}

}
