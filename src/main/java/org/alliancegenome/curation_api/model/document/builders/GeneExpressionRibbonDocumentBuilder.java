package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.document.es.GeneExpressionRibbonSummaryDocument;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.VocabularyTermSetService;
import org.alliancegenome.curation_api.services.ontology.GoTermService;
import org.alliancegenome.curation_api.services.ontology.UberonTermService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneExpressionRibbonDocumentBuilder {

	@Inject VocabularyTermSetService vocabularyTermSetService;
	@Inject UberonTermService uberonTermService;
	@Inject GoTermService goTermService;

	public GeneExpressionRibbonSummaryDocument build() {

		GeneExpressionRibbonSummaryDocument document = new GeneExpressionRibbonSummaryDocument();

		Pagination pagination = new Pagination();
		HashMap<String, Object> params = new HashMap<>();
		params.put("vocabularyLabel", VocabularyConstants.ANATOMICAL_STRUCTURE_SLIM_TERMS_PUBLIC_SITE);
		
		SearchResponse<VocabularyTermSet> asVocabTermSet = vocabularyTermSetService.findByParams(pagination, params);
		List<String> anatomicalTerms = asVocabTermSet.getSingleResult().getMemberTerms().stream().map(e -> e.getName()).collect(Collectors.toList());
		List<UBERONTerm> anatomicalUberonTerms = new ArrayList<>();
		for (String term : anatomicalTerms) {
			if (term.equals("Other")) {
				UBERONTerm otherTerm = new UBERONTerm();
				otherTerm.setCurie("AnatomyOtherLocation");
				otherTerm.setName("Other");
				anatomicalUberonTerms.add(otherTerm);
			}
			else {
				anatomicalUberonTerms.add(uberonTermService.getByCurie(term).getEntity());
			}
		}
		document.setAnatomicalStructureSlimTerms(anatomicalUberonTerms);

		params.put("vocabularyLabel", VocabularyConstants.STAGE_SLIM_TERMS_PUBLIC_SITE);
		SearchResponse<VocabularyTermSet> stageVocabTermSet = vocabularyTermSetService.findByParams(pagination, params);
		List<String> stageTerms = stageVocabTermSet.getSingleResult().getMemberTerms().stream().map(e -> e.getName()).collect(Collectors.toList());
		List<UBERONTerm> stageUberonTerms = new ArrayList<>();
		for (String term : stageTerms) {
			if (term.equals("post embryonic, pre-adult")) {
				UBERONTerm otherTerm = new UBERONTerm();
				otherTerm.setCurie("PostEmbryonicPreAdult");
				otherTerm.setName("post embryonic, pre-adult");
				stageUberonTerms.add(otherTerm);
			}
			else {
				stageUberonTerms.add(uberonTermService.getByCurie(term).getEntity());
			}
		}
		document.setStageSlimTerms(stageUberonTerms);

		params.put("vocabularyLabel", VocabularyConstants.GO_SLIM_TERMS_PUBLIC_SITE);
		SearchResponse<VocabularyTermSet> goVocabTermSet = vocabularyTermSetService.findByParams(pagination, params);
		List<String> goTerms = goVocabTermSet.getSingleResult().getMemberTerms().stream().map(e -> e.getName()).collect(Collectors.toList());
		List<GOTerm> goSlimTerms = new ArrayList<>();
		for (String term : goTerms) {
			if (term.equals("otherLocations")) {
				GOTerm otherTerm = new GOTerm();
				otherTerm.setCurie("otherLocations");
				otherTerm.setName("other locations");
				goSlimTerms.add(otherTerm);
			}
			else {
				goSlimTerms.add(goTermService.getByCurie(term).getEntity());
			}
		}
		document.setGoSlimTerms(goSlimTerms);
		
		return document;
	}
}
