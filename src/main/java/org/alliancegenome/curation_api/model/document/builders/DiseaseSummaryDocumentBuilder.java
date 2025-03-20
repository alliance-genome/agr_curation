package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashMap;

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
		doc.setCrossReferenceLinkUrls(new HashMap<>());
		for (CrossReference cr : doTerm.getCrossReferences()) {
			doc.getCrossReferenceLinkUrls().put(cr.getReferencedCurie(), cr.getUrlFromResourceDescriptorPage(doTerm.getCurie()));
		}

		return doc;
	}
	
}
