package org.alliancegenome.curation_api.model.document.es;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(View.DiseaseSummaryDocument.class)
public class DiseaseSummaryDocument extends ESDocument {

	{
		category = "disease_summary";
	}
	
	private DOTerm doTerm;
	private Set<OntologyTerm> parents;
	private Set<OntologyTerm> children;
	private List<Map<String, String>> crossReferenceLinkUrls;
	private Map<String, String> sourceReferenceLinkUrls;
}
