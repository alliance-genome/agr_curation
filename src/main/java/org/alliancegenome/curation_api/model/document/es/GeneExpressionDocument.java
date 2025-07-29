package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
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
	private InformationContentEntity reference;
	private String termName;
	private StageTerm stage;
	private MMOTerm assay;
	private Organization dataProvider;
	private List<CrossReference> crossReferences;
	private String stageTermName;

}
