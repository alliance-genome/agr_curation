package org.alliancegenome.curation_api.model.document.builders;

import java.util.Map;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;


import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class GeneExpressionDocumentBuilder {
	
	@Inject private GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;

	public GeneExpressionDocument buildDocument(GeneExpressionAnnotation annotation, Map<String, GeneExpressionExperiment> experiments) {
		GeneExpressionDocument expressionDetailDocument = new GeneExpressionDocument();
		if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
			ConsolidatedGeneExpressionFmsDTO geneExpressionFmsDTO = new ConsolidatedGeneExpressionFmsDTO();
			geneExpressionFmsDTO.setGeneId(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
			geneExpressionFmsDTO.setAssay(annotation.getExpressionAssayUsed().getCurie());
			String experimentId = geneExpressionAnnotationUniqueIdHelper.generateExperimentId(geneExpressionFmsDTO, annotation.getEvidenceItem().getCurie());
			if (experiments.get(experimentId) != null && experiments.get(experimentId).getCrossReferences() != null) {
				expressionDetailDocument.setCrossReferences(experiments.get(experimentId).getCrossReferences());
			}
		} else {
			// This is for ZFIN only at the moment
			expressionDetailDocument.setCrossReferences(annotation.getCrossReferences());
		}
		expressionDetailDocument.setReference(annotation.getEvidenceItem());
		expressionDetailDocument.setDataProvider(annotation.getDataProvider());
		expressionDetailDocument.setGene(annotation.getExpressionAnnotationSubject());
		expressionDetailDocument.setAssay(annotation.getExpressionAssayUsed());
		expressionDetailDocument.setTermName(annotation.getWhereExpressedStatement());
		if (isNotEmpty(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms())) {
			expressionDetailDocument.setStageTermID(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().getFirst().getName());
		}

		expressionDetailDocument.setStage(annotation.getExpressionPattern().getWhenExpressed().getDevelopmentalStageStart());

		return expressionDetailDocument;
	}
}
