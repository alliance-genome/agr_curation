package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.model.document.es.DiseaseSummaryDocument;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DiseaseSummaryDocumentBuilder {

	public DiseaseSummaryDocument buildSummaryDocument(DOTerm doTerm) {
		
		DiseaseSummaryDocument doc = new DiseaseSummaryDocument();

		doc.setDoTerm(doTerm);
		doc.setParents(doTerm.getIsaParents());
		doc.setChildren(doTerm.getIsaChildren());
		doc.setCrossReferenceLinkUrls(new ArrayList<Map<String, String>>());
		for (CrossReference cr : doTerm.getCrossReferences()) {
			Map<String, String> map = new HashMap<>();
			map.put("referencedCurie", cr.getReferencedCurie());
			map.put("url", cr.getUrlFromResourceDescriptorPage(doTerm.getCurie()));
			doc.getCrossReferenceLinkUrls().add(map);
		}

		return doc;
	}
	
}
