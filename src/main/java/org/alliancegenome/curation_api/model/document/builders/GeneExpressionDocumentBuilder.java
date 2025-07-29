package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashMap;
import java.util.Map;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneExpressionDocumentBuilder {

	@Inject UniqueIdGeneratorHelper uniqueIdGeneratorHelper;
	
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
	
			GeneExpressionExperiment experiment = resp.getSingleResult();
			
			if (experiment != null && experiment.getCrossReferences() != null) {
				expressionDocument.setCrossReferences(experiment.getCrossReferences());
			}
		} else {
			expressionDocument.setCrossReferences(annotation.getCrossReferences());
		}
		expressionDocument.setReference(annotation.getEvidenceItem());
		expressionDocument.setDataProvider(annotation.getDataProvider());
		expressionDocument.setGene(annotation.getExpressionAnnotationSubject());
		expressionDocument.setAssay(annotation.getExpressionAssayUsed());
		expressionDocument.setTermName(annotation.getWhereExpressedStatement());
		if (isNotEmpty(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms())) {
			expressionDocument.setStageTermID(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().getFirst().getName());
		}

		expressionDocument.setStage(annotation.getExpressionPattern().getWhenExpressed().getDevelopmentalStageStart());

		return expressionDocument;

	}
}
