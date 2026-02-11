package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneExpressionDocumentBuilder {

	public static final String UBERON_ANATOMY_ROOT = "UBERON:0001062";

	public static final String UBERON_STAGE_ROOT = "UBERON:0000000";

	public static final String GO_CC_ROOT = "GO:0005575";

	public static final String UBERON_ANATOMY_OTHER = "AnatomyOtherLocation";

	public static final String UBERON__POST_EMBRYONIC_PRE_ADULT = "PostEmbryonicPreAdult";

	public static final String GO_CELLULAR_OTHER = "otherLocations";
	
	public GeneExpressionDocument buildDocument(GeneExpressionAnnotation annotation) {

		List<String> uberonTermIds = new ArrayList<>();
		List<String> goTermIds = new ArrayList<>();
		List<String> termIds = new ArrayList<>();
		
		GeneExpressionDocument expressionDocument = new GeneExpressionDocument();
		if (annotation != null) {
			expressionDocument.setGeneExpressionAnnotation(annotation);
			if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
				expressionDocument.getGeneExpressionAnnotation().setCrossReferences(annotation.getExpressionExperiment().getCrossReferences());
			} else {
				expressionDocument.getGeneExpressionAnnotation().setCrossReferences(annotation.getCrossReferences());
			}
		}

		// There is only single stage term coming from FMS files and that we are storing it in list as single element. Hence pulling the only element.
		if (annotation.getExpressionPattern() != null && annotation.getExpressionPattern().getWhenExpressed() != null) {

			List<VocabularyTerm> stageUberonTerms = annotation.getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms();
			if (CollectionUtils.isNotEmpty(stageUberonTerms) && ObjectUtils.isNotEmpty(stageUberonTerms.getFirst())) {
				String stageTerm = stageUberonTerms.getFirst().getName();
				if (stageTerm.equals("post embryonic, pre-adult")) {
					stageTerm = UBERON__POST_EMBRYONIC_PRE_ADULT;
					expressionDocument.getGeneExpressionAnnotation().getExpressionPattern().getWhenExpressed().getStageUberonSlimTerms().get(0).setName(stageTerm);
				}
				termIds.add(stageTerm);
				termIds.add(UBERON_STAGE_ROOT);
			}
		}

		AnatomicalSite whereExpressed = annotation.getExpressionPattern() != null ? annotation.getExpressionPattern().getWhereExpressed() : null;

		if (whereExpressed != null) {
			if (!whereExpressed.getAnatomicalStructureUberonTermOther()) {
				if (CollectionUtils.isNotEmpty(whereExpressed.getAnatomicalStructureUberonTerms())) {
					uberonTermIds.addAll(whereExpressed.getAnatomicalStructureUberonTerms().stream()
							.map(term -> term.getCurie()).toList());
					uberonTermIds.add(UBERON_ANATOMY_ROOT);
					expressionDocument.setUberonTermIds(uberonTermIds);
				}
			} else {
				uberonTermIds.add(UBERON_ANATOMY_OTHER);
				uberonTermIds.add(UBERON_ANATOMY_ROOT);
				expressionDocument.setUberonTermIds(uberonTermIds);
			}

			if (!whereExpressed.getCellularComponentOther()) {
				if (ObjectUtils.isNotEmpty(whereExpressed.getCellularComponentRibbonTerms())) {
					goTermIds.addAll(whereExpressed.getCellularComponentRibbonTerms().stream().map(term -> term.getCurie()).collect(Collectors.toList()));
					goTermIds.add(GO_CC_ROOT);
					expressionDocument.setGoTermIds(goTermIds);
				}
			} else {
				goTermIds.add(GO_CELLULAR_OTHER);
				goTermIds.add(GO_CC_ROOT);
				expressionDocument.setGoTermIds(goTermIds);
			}

			termIds.addAll(uberonTermIds);
			termIds.addAll(goTermIds);
		}
		expressionDocument.setTermIds(termIds);
		if (annotation.getEvidenceItem() != null && annotation.getEvidenceItem() instanceof Reference reference) {
			expressionDocument.setReferenceId(List.of(reference.getReferenceID()));
		}
		expressionDocument.setPhylogeneticSortingIndex(annotation.getExpressionAnnotationSubject().getTaxon().getPhylogeneticSortOrder());
		
		if (annotation.getExpressionAssayUsed() != null) {
			String assayName = annotation.getExpressionAssayUsed().getSynonyms().stream().filter(synonym -> synonym.getIsDisplaySynonym()).findFirst().map(Synonym::getName).orElse(null);
			if (assayName != null) {
				expressionDocument.getGeneExpressionAnnotation().getExpressionAssayUsed().setName(assayName);
			}
		}
		return expressionDocument;

	}

	public List<GeneExpressionDocument> consolidateExpressionDocuments(List<GeneExpressionDocument> documents) {
		Map<String, List<GeneExpressionDocument>> groupedDocuments = documents.stream()
			.collect(Collectors.groupingBy(doc -> {
				String geneId = doc.getGeneExpressionAnnotation().getExpressionAnnotationSubject() != null ? doc.getGeneExpressionAnnotation().getExpressionAnnotationSubject().getPrimaryExternalId() : "";
				String location = doc.getGeneExpressionAnnotation().getWhereExpressedStatement() != null ? doc.getGeneExpressionAnnotation().getWhereExpressedStatement() : "";
				String stage = doc.getGeneExpressionAnnotation().getWhenExpressedStageName() != null ? doc.getGeneExpressionAnnotation().getWhenExpressedStageName() : "";
				String assay = doc.getGeneExpressionAnnotation().getExpressionAssayUsed() != null ? doc.getGeneExpressionAnnotation().getExpressionAssayUsed().getCurie() : "";

				return geneId + "||" + location + "||" + stage + "||" + assay;
			}));

		List<GeneExpressionDocument> consolidatedDocuments = new ArrayList<>();

		for (Map.Entry<String, List<GeneExpressionDocument>> entry : groupedDocuments.entrySet()) {
			List<GeneExpressionDocument> group = entry.getValue();

			if (group.size() == 1) {
				consolidatedDocuments.add(group.get(0));
			} else {
				GeneExpressionDocument consolidated = group.get(0);

				List<CrossReference> allCrossReferences = new ArrayList<>();
				List<String> allReferenceIds = new ArrayList<>();

				// logic behind this consolidation is to have 1:1 mapping for reference and crossReference for consolidated annotations, 
				// which is useful while deconsolidating later for download endpoint
				for (GeneExpressionDocument doc : group) {
					int annotationSize = Math.max(
						CollectionUtils.isNotEmpty(doc.getGeneExpressionAnnotation().getCrossReferences()) ? doc.getGeneExpressionAnnotation().getCrossReferences().size() : 0,
						CollectionUtils.isNotEmpty(doc.getReferenceId()) ? doc.getReferenceId().size() : 0
					);
					for (int i = 0; i < annotationSize; i++) {
						if (CollectionUtils.isNotEmpty(doc.getGeneExpressionAnnotation().getCrossReferences()) && i < doc.getGeneExpressionAnnotation().getCrossReferences().size()) {
							allCrossReferences.add(doc.getGeneExpressionAnnotation().getCrossReferences().get(i));
						} else {
							CrossReference emptyRef = new CrossReference();
							emptyRef.setDisplayName("");
							emptyRef.setReferencedCurie("");
							allCrossReferences.add(emptyRef);
						}
						if (CollectionUtils.isNotEmpty(doc.getReferenceId())) {
							// Incoming geneexpression annotation always has only one referenceId
							allReferenceIds.add(doc.getReferenceId().get(0));
						}
					}
				}

				consolidated.getGeneExpressionAnnotation().setCrossReferences(allCrossReferences);
				consolidated.setReferenceId(allReferenceIds);

				consolidatedDocuments.add(consolidated);
			}
		}

		return consolidatedDocuments;
	}
	
	
	
}
