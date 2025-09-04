package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import groovy.transform.EqualsAndHashCode;
import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.GeneExpressionDocument.class)
public class GeneExpressionDocument extends ESDocument {
	{
		category = "gene_expression_annotation";
	}
	private Gene gene;
	private String location;
	private String stageName;
	private MMOTerm assay;
	private String dataProvider;
	private List<CrossReference> crossRefs;
	private String stageTermId;
	private List<String> uberonTermIds;
	private List<String> goTermIds;
	private List<String> termIds;
}
