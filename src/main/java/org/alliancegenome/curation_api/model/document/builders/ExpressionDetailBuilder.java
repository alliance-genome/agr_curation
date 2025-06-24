package org.alliancegenome.curation_api.model.document.builders;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.alliancegenome.curation_api.model.document.es.ExpressionDetail;
import org.alliancegenome.curation_api.model.document.es.Publication;
import org.alliancegenome.curation_api.model.document.es.Stage;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.ConsolidatedGeneExpressionFmsDTO;
import org.alliancegenome.curation_api.services.ReferenceService;
import org.alliancegenome.curation_api.services.helpers.annotations.GeneExpressionAnnotationUniqueIdHelper;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@RequestScoped
public class ExpressionDetailBuilder {

	@Inject private GeneExpressionAnnotationUniqueIdHelper geneExpressionAnnotationUniqueIdHelper;
	@Inject private ReferenceService referenceService;

	public ExpressionDetail build(GeneExpressionAnnotation annotation, Map<String, GeneExpressionExperiment> experiments) {
		ExpressionDetail expressionDetail = new ExpressionDetail();
		if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
			ConsolidatedGeneExpressionFmsDTO geneExpressionFmsDTO = new ConsolidatedGeneExpressionFmsDTO();
			geneExpressionFmsDTO.setGeneId(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
			geneExpressionFmsDTO.setAssay(annotation.getExpressionAssayUsed().getCurie());
			String experimentId = geneExpressionAnnotationUniqueIdHelper.generateExperimentId(geneExpressionFmsDTO, annotation.getEvidenceItem().getCurie());
			if (experiments.get(experimentId) != null && experiments.get(experimentId).getCrossReferences() != null) {
				expressionDetail.setCrossReferences(experiments.get(experimentId).getCrossReferences());
			}
		} else {
			expressionDetail.setCrossReferences(annotation.getCrossReferences());
		}
		expressionDetail.setDataProvider(annotation.getDataProvider().getAbbreviation());
		expressionDetail.setGene(annotation.getExpressionAnnotationSubject());
		expressionDetail.setAssay(annotation.getExpressionAssayUsed());
		expressionDetail.setTermName(annotation.getWhereExpressedStatement());
		if (isNotEmpty(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms())) {
			expressionDetail.setStageTermID(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().getFirst().getName());
		}

		Stage stage = new Stage();
		stage.setName(annotation.getWhenExpressedStageName());
		if (isNotEmpty(annotation.getExpressionPattern().getWhenExpressed().getDevelopmentalStageStart())) {
			stage.setPrimaryKey(annotation.getExpressionPattern().getWhenExpressed().getDevelopmentalStageStart().getCurie());
		} else {
			stage.setPrimaryKey(annotation.getWhenExpressedStageName());
		}
		expressionDetail.setStage(stage);
		Publication publication = new Publication();
		String refCurie = annotation.getEvidenceItem().getCurie();
		if (!referenceService.getByCurie(refCurie).hasErrors()) {
			Reference reference = referenceService.getByCurie(refCurie).getEntity();
			publication.setPubModId(reference.getPubModID());
			publication.setPubMedId(reference.getReferenceID());
			publication.setPrimaryKey(reference.getCurie());
		}
		TreeSet<Publication> publications = new TreeSet<>();
		publications.add(publication);
		expressionDetail.setPublications(publications);

		// TODO
		expressionDetail.addTermIDs(List.of());
		expressionDetail.setUberonTermIDs(annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().stream().map(VocabularyTerm::getAbbreviation).toList());
		expressionDetail.setGoTermIDs(List.of(annotation.getExpressionPattern().getWhereExpressed().getCellularComponentRibbonTerm().getName()));

		return expressionDetail;
	}
}
