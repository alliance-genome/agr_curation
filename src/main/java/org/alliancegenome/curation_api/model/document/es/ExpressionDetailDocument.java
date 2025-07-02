package org.alliancegenome.curation_api.model.document.es;

import java.io.Serializable;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.StageTerm;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonView({org.alliancegenome.curation_api.view.View.ExpressionDetailDocument.class})
public class ExpressionDetailDocument extends ESDocument implements Comparable<ExpressionDetailDocument>, Serializable {
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
	private String stageTermID;

	@Override
	public int compareTo(@NotNull ExpressionDetailDocument expressionDetailDocument) {
		return 0;
	}
}
