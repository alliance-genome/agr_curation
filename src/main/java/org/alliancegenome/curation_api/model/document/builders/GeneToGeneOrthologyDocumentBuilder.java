package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.document.es.GeneToGeneOrthologyDocument;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.apache.commons.collections4.CollectionUtils;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneToGeneOrthologyDocumentBuilder {

	public static GeneToGeneOrthologyDocument buildSearchResultDocument(GeneToGeneOrthologyGenerated g2gOrtho) {
		GeneToGeneOrthologyDocument doc = new GeneToGeneOrthologyDocument();
		doc.setGeneToGeneOrthologyGenerated(g2gOrtho);
		
		createStringencyFilter(g2gOrtho, doc);
		createGeneAnnotations(g2gOrtho, doc);
		removeAnnotationLists(doc);

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
		data.put("hasExpressionAnnotations", hasExpressionAnnotations(gene));
		data.put("hasDiseaseAnnotations", hasDiseaseAnnotations(gene));
		list.add(data);
	}

	private static boolean hasDiseaseAnnotations(Gene gene) {
		return CollectionUtils.isNotEmpty(gene.getGeneDiseaseAnnotations());
	}

	private static boolean hasExpressionAnnotations(Gene gene) {
		return CollectionUtils.isNotEmpty(gene.getGeneExpressionAnnotations());
	}

	private static void removeAnnotationLists(GeneToGeneOrthologyDocument document) {
		document.getGeneToGeneOrthologyGenerated().getSubjectGene().setGeneDiseaseAnnotations(null);
		document.getGeneToGeneOrthologyGenerated().getSubjectGene().setGeneExpressionAnnotations(null);
		document.getGeneToGeneOrthologyGenerated().getObjectGene().setGeneDiseaseAnnotations(null);
		document.getGeneToGeneOrthologyGenerated().getObjectGene().setGeneExpressionAnnotations(null);
	}

}
