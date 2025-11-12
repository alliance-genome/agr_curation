package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.alliancegenome.curation_api.dao.GeneExpressionExperimentDAO;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionDocument;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionExperiment;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneExpressionDocumentBuilder {

	public static final String UBERON_ANATOMY_ROOT = "UBERON:0001062";

	public static final String UBERON_STAGE_ROOT = "UBERON:0000000";

	public static final String GO_CC_ROOT = "GO:0005575";

	public static final String UBERON_ANATOMY_OTHER = "UBERON:AnatomyOtherLocation";

	public static final String UBERON__POST_EMBRYONIC_PRE_ADULT = "UBERON:PostEmbryonicPreAdult";

	public static final String GO_CELLULAR_OTHER = "GO:otherLocations";
	
	public GeneExpressionDocument buildDocument(GeneExpressionAnnotation annotation, Map<String, GeneExpressionExperiment> experimentsCache) {

		List<String> uberonTermIds = new ArrayList<>();
		List<String> goTermIds = new ArrayList<>();
		List<String> termIds = new ArrayList<>();
		
		GeneExpressionDocument expressionDocument = new GeneExpressionDocument();
		if (annotation != null) {
			expressionDocument.setGeneExpressionAnnotation(annotation);
		
			if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {
				
				UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();
				uniqueIdGeneratorHelper.add(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
				uniqueIdGeneratorHelper.add(annotation.getEvidenceItem().getCurie());
				uniqueIdGeneratorHelper.add(annotation.getExpressionAssayUsed().getCurie());
				String uniqueId = uniqueIdGeneratorHelper.getUniqueId();

				GeneExpressionExperiment experiment = experimentsCache.get(uniqueId);

				if (experiment != null && experiment.getCrossReferences() != null) {
					expressionDocument.getGeneExpressionAnnotation().setCrossReferences(experiment.getCrossReferences());
				}
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
				if (ObjectUtils.isNotEmpty(whereExpressed.getCellularComponentRibbonTerm())) {
					goTermIds.add(whereExpressed.getCellularComponentRibbonTerm().getCurie());
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
		
		return expressionDocument;

	}

	public Map<String, GeneExpressionExperiment> preloadExperiments(List<GeneExpressionAnnotation> annotations, GeneExpressionExperimentDAO geneExpressionExperimentDAO) {
		Set<String> uniqueIds = new HashSet<>();
		UniqueIdGeneratorHelper uniqueIdGeneratorHelper = new UniqueIdGeneratorHelper();

		for (GeneExpressionAnnotation annotation : annotations) {
			if (annotation.getDataProvider().getAbbreviation().equals("MGI") || annotation.getDataProvider().getAbbreviation().equals("WB")) {

				uniqueIdGeneratorHelper.clear();
				uniqueIdGeneratorHelper.add(annotation.getExpressionAnnotationSubject().getPrimaryExternalId());
				uniqueIdGeneratorHelper.add(annotation.getEvidenceItem().getCurie());
				uniqueIdGeneratorHelper.add(annotation.getExpressionAssayUsed().getCurie());
				uniqueIds.add(uniqueIdGeneratorHelper.getUniqueId());
			}
		}

		if (uniqueIds.isEmpty()) {
			return new HashMap<>();
		}

		Map<String, GeneExpressionExperiment> result = new HashMap<>();
		for (String uniqueId : uniqueIds) {
			HashMap<String, Object> params = new HashMap<>();
			params.put("uniqueId", uniqueId);
			SearchResponse<GeneExpressionExperiment> experiments = geneExpressionExperimentDAO.findByParams(params);
			
			if (CollectionUtils.isNotEmpty(experiments.getResults())) {
				GeneExpressionExperiment experiment = experiments.getSingleResult();
				if (experiment != null) {
					result.put(experiment.getUniqueId(), experiment);
				}
			}
		}
		
		return result;
	}
}
