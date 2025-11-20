package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.UBERONTerm;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.ForPublic.class)
public class GeneExpressionRibbonSummaryDocument extends ESDocument {

	{
		category = "gene_expression_ribbon_summary";
	}
	private List<UBERONTerm> anatomicalStructureSlimTerms;
	private List<UBERONTerm> stageSlimTerms;
	private List<GOTerm> goSlimTerms;
	
}
