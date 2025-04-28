package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.document.es.GeneToGeneOrthologyDocument;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneToGeneOrthologyDocumentBuilder {

	public static GeneToGeneOrthologyDocument buildSearchResultDocument(GeneToGeneOrthologyGenerated g2gOrtho) {
		GeneToGeneOrthologyDocument doc = new GeneToGeneOrthologyDocument();
		doc.setGeneToGeneOrthologyGenerated(g2gOrtho);
		createStringencyFilter(g2gOrtho, doc);
		createGeneAnnotations(g2gOrtho, doc);
		return doc;
	}

	private static void createStringencyFilter(GeneToGeneOrthologyGenerated g2gOrtho, GeneToGeneOrthologyDocument document) {
		if (Boolean.TRUE.equals(g2gOrtho.getStrictFilter())) {
			document.setStringencyFilter("stringent");
		} else if (Boolean.TRUE.equals(g2gOrtho.getModerateFilter())) {
			document.setStringencyFilter("moderate");
		}
	}

	private static void createGeneAnnotations(GeneToGeneOrthologyGenerated g2gOrtho, GeneToGeneOrthologyDocument document) {
		List<Map<String, Object>> geneAnnotationsList = new ArrayList<>();
		putGeneInfo(geneAnnotationsList, g2gOrtho.getSubjectGene());
		putGeneInfo(geneAnnotationsList, g2gOrtho.getObjectGene());
		document.setGeneAnnotations(geneAnnotationsList);
	}

	private static void putGeneInfo(List<Map<String, Object>> list, Gene gene) {
		Map<String, Object> data = new HashMap<>();
		data.put("geneIdentifier", gene.getIdentifier());
		data.put("hasExpressionAnnotations", gene.getGeneExpressionAnnotations().size() > 0);
		data.put("hasDiseaseAnnotations", gene.getGeneDiseaseAnnotations().size() > 0);
		list.add(data);
	}

}
