package org.alliancegenome.curation_api.model.document.es;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.view.View;
import org.jetbrains.annotations.NotNull;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonView({View.ExpressionDetail.class})
public class ExpressionDetail extends ESDocument implements Comparable<ExpressionDetail>, Serializable {
	{
		category = "gene_expression_annotation";
	}
	private Gene gene;
	private String termName;
	private Stage stage;
	private MMOTerm assay;
	private TreeSet<Publication> publications;
	private String dataProvider;
	private List<CrossReference> crossReferences;
	private String stageTermID;
	private List<String> termIDs = new java.util.ArrayList<>(6);
	private List<String> uberonTermIDs = new java.util.ArrayList<>(6);
	private List<String> goTermIDs = new java.util.ArrayList<>(6);

	public void addTermIDs(Collection<String> ids) {
		termIDs.addAll(ids);
	}

	public void addTermID(String id) {
		termIDs.add(id);
	}

	@Override
	public int compareTo(@NotNull ExpressionDetail expressionDetail) {
		return 0;
	}
}
