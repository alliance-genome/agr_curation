package org.alliancegenome.curation_api.model.document.es;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;

import java.util.*;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.ModelDocumentView.class)
public class ModelDocument extends ESDocument {
	{
		category = "gene_model_annotation";
	}

	private AffectedGenomicModel model;
	private List<String> associatedPhenotype = new ArrayList<>();

	private Set<DOTerm> diseaseTerms;
	private Organization dataProvider;
	private Set<ConditionRelation> conditionRelations;

	public void addDiseaseTerms(DOTerm diseaseAnnotationObject) {
		if (diseaseTerms == null) {
			diseaseTerms = new HashSet<>();
		}
		diseaseTerms.add(diseaseAnnotationObject);
	}

	public void addConditionRelations(Collection<ConditionRelation> relations) {
		if (conditionRelations == null) {
			conditionRelations = new HashSet<>();
		}
		conditionRelations.addAll(relations);
	}

}
