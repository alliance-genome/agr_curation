package org.alliancegenome.curation_api.model.document.es;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.GeneToGeneOrthologyDocument.class)
public class GeneToGeneOrthologyDocument extends ESDocument {
	{
		category = "gene_to_gene_orthology";
	}
	private String stringencyFilter = "all";
	private List<Map<String, Object>> geneAnnotations;
	private Map<String, Map<String, Object>> geneAnnotationsMap;
	private GeneToGeneOrthologyGenerated geneToGeneOrthologyGenerated;
}
