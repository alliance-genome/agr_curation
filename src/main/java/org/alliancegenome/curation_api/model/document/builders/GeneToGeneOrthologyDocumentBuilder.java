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

	public GeneToGeneOrthologyDocument buildSearchResultDocument(GeneToGeneOrthologyGenerated geneToGeneOrthology) {
		GeneToGeneOrthologyDocument doc = new GeneToGeneOrthologyDocument();
		doc.setGeneToGeneOrthologyGenerated(geneToGeneOrthology);
		createStringencyFilter(geneToGeneOrthology, doc);
		createGeneAnnotations(geneToGeneOrthology, doc);
		return doc;
	}

	private void createStringencyFilter(GeneToGeneOrthologyGenerated g2gOrtho, GeneToGeneOrthologyDocument document) {
		if (Boolean.TRUE.equals(g2gOrtho.getStrictFilter())) {
			document.setStringencyFilter("stringent");
		} else if (Boolean.TRUE.equals(g2gOrtho.getModerateFilter())) {
			document.setStringencyFilter("moderate");
		}
	}

	private void createGeneAnnotations(GeneToGeneOrthologyGenerated geneToGeneOrthology, GeneToGeneOrthologyDocument document) {
		List<Map<String, Object>> geneAnnotationsList = new ArrayList<>();
		putGeneInfo(geneAnnotationsList, geneToGeneOrthology.getSubjectGene());
		putGeneInfo(geneAnnotationsList, geneToGeneOrthology.getObjectGene());
		document.setGeneAnnotations(geneAnnotationsList);
	}

	private void putGeneInfo(List<Map<String, Object>> list, Gene gene) {
		Map<String, Object> data = new HashMap<>();
		data.put("geneIdentifier", gene.getIdentifier());
		list.add(data);
	}
}
