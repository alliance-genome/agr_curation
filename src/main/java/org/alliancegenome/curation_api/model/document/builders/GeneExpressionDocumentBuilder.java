package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashMap;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneExpressionDocumentBuilder {

	
	public GeneExpressionDocument buildDocument(GeneExpressionAnnotation annotation, GeneExpressionExperimentDAO geneExpressionExperimentDAO) {
		
		GeneExpressionDocument expressionDocument = new GeneExpressionDocument();
		if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
			
			UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();
			uniqueIdGeneratorHelper.add(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
			uniqueIdGeneratorHelper.add(annotation.getEvidenceItem().getCurie());
			uniqueIdGeneratorHelper.add(annotation.getExpressionAssayUsed().getCurie());
			String uniqueId = uniqueIdGeneratorHelper.getUniqueId();
			
			HashMap<String, Object> params = new HashMap<>();
			params.put("uniqueId", uniqueId);
	
			SearchResponse<GeneExpressionExperiment> resp = geneExpressionExperimentDAO.findByParams(params);
	
			GeneExpressionExperiment experiment = null;
			if (resp != null) {
				experiment = resp.getSingleResult();
				if (experiment != null && experiment.getCrossReferences() != null) {
					expressionDocument.setCrossReferences(experiment.getCrossReferences());
				}
			}
		} else {
			expressionDocument.setCrossReferences(annotation.getCrossReferences());
		}
		expressionDocument.setReference(annotation.getEvidenceItem());
		expressionDocument.setDataProvider(annotation.getDataProvider());
		expressionDocument.setGene(annotation.getExpressionAnnotationSubject());
		expressionDocument.setAssay(annotation.getExpressionAssayUsed());
		expressionDocument.setLocation(annotation.getWhereExpressedStatement());
		expressionDocument.setStageName(annotation.getWhenExpressedStageName());
		List<VocabularyTerm> stageUberonTerms = annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms();
		if (isNotEmpty(stageUberonTerms)) {
			expressionDocument.setStageUberonTerm(stageUberonTerms.getFirst().getName());
		}

		return expressionDocument;

	}
}
