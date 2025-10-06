package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
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
	private GeneExpressionAnnotation geneExpressionAnnotation;
	private String pubModID;
	private List<String> uberonTermIds;
	private List<String> goTermIds;
	private List<String> termIds;

}
