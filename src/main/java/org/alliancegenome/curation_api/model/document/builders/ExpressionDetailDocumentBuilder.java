package org.alliancegenome.curation_api.model.document.builders;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@RequestScoped
public class ExpressionDetailDocumentBuilder {

	@Inject private GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;
	@Inject private ReferenceService referenceService;

	public org.alliancegenome.curation_api.model.document.es.ExpressionDetailDocument build(GeneExpressionAnnotation annotation, Map<String, GeneExpressionExperiment> experiments) {
		org.alliancegenome.curation_api.model.document.es.ExpressionDetailDocument expressionDetailDocument = new org.alliancegenome.curation_api.model.document.es.ExpressionDetailDocument();
		if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
			ConsolidatedGeneExpressionFmsDTO geneExpressionFmsDTO = new ConsolidatedGeneExpressionFmsDTO();
			geneExpressionFmsDTO.setGeneId(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
			geneExpressionFmsDTO.setAssay(annotation.getExpressionAssayUsed().getCurie());
			String experimentId = geneExpressionAnnotationUniqueIdHelper.generateExperimentId(geneExpressionFmsDTO, annotation.getEvidenceItem().getCurie());
			if (experiments.get(experimentId) != null && experiments.get(experimentId).getCrossReferences() != null) {
				expressionDetailDocument.setCrossReferences(experiments.get(experimentId).getCrossReferences());
			}
		} else {
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


		// TODO
//		expressionDetailDocument.addTermIDs(List.of());
//		expressionDetail.setUberonTermIDs(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().stream().map(VocabularyTerm::getAbbreviation).toList());
//		expressionDetail.setGoTermIDs(List.of(annotation.getExpressionPattern().getWhereExpressed().getCellularComponentRibbonTerm().getName()));

		return expressionDetailDocument;
	}
}
