package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.document.es.GeneToGeneOrthologyDocument;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneToGeneOrthologyDocumentBuilder {

	public GeneToGeneOrthologyDocument buildSearchResultDocument(GeneToGeneOrthologyGenerated geneToGeneOrthology, Set<String> geneIdMap) {
		GeneToGeneOrthologyDocument doc = new GeneToGeneOrthologyDocument();
		doc.setGeneToGeneOrthologyGenerated(geneToGeneOrthology);
		createStringencyFilter(geneToGeneOrthology, doc);
		createGeneAnnotations(geneToGeneOrthology, doc, geneIdMap);
		return doc;
	}

	private void createStringencyFilter(GeneToGeneOrthologyGenerated g2gOrtho, GeneToGeneOrthologyDocument document) {
		if (Boolean.TRUE.equals(g2gOrtho.getStrictFilter())) {
			document.setStringencyFilter("stringent");
		} else if (Boolean.TRUE.equals(g2gOrtho.getModerateFilter())) {
			document.setStringencyFilter("moderate");
		}
	}

	private void createGeneAnnotations(GeneToGeneOrthologyGenerated geneToGeneOrthology, GeneToGeneOrthologyDocument document, Set<String> geneIdMap) {
		List<Map<String, Object>> geneAnnotationsList = new ArrayList<>();
		boolean hasGeneExpression = geneIdMap.contains(geneToGeneOrthology.getSubjectGene().getPrimaryExternalId());
		putGeneInfo(geneAnnotationsList, geneToGeneOrthology.getSubjectGene(), hasGeneExpression);
		boolean hasGene2Expression = geneIdMap.contains(geneToGeneOrthology.getObjectGene().getPrimaryExternalId());
		putGeneInfo(geneAnnotationsList, geneToGeneOrthology.getObjectGene(), hasGene2Expression);
		document.setGeneAnnotations(geneAnnotationsList);
	}

	private void putGeneInfo(List<Map<String, Object>> list, Gene gene, boolean hasGeneExpression) {
		Map<String, Object> data = new HashMap<>();
		data.put("geneIdentifier", gene.getIdentifier());
		data.put("hasExpressionAnnotations", hasGeneExpression);
		data.put("hasDiseaseAnnotations", gene.getGeneDiseaseAnnotations().size() > 0);
		list.add(data);
	}
}
