package org.alliancegenome.curation_api.model.document.es;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.collections4.CollectionUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(View.ModelDocumentView.class)
public class AffectedGenomicModelDocument extends ESDocument {
	{
		category = "affected_genomic_model_annotation";
	}

	private AffectedGenomicModel model;
	private List<String> associatedPhenotype = new ArrayList<>();

	private Gene gene;
	private Set<DOTerm> diseaseTerms;
	private String dataProvider;
	private Set<ConditionRelation> conditionRelations;
	private Set<ConditionRelation> conditionModifiers;

	public void addDiseaseTerm(DOTerm diseaseAnnotationObject) {
		if (diseaseTerms == null) {
			diseaseTerms = new HashSet<>();
		}
		diseaseTerms.add(diseaseAnnotationObject);
	}

	private List<String> modifierRelationshipTypes = List.of("ameliorated_by", "exacerbated_by");

	public void addConditionRelations(Collection<ConditionRelation> relations) {
		Set<ConditionRelation> modifierRelations = relations.stream().filter(conditionRelation -> modifierRelationshipTypes.contains(conditionRelation.getConditionRelationType().getName())).collect(Collectors.toSet());
		Set<ConditionRelation> conditionRelationSet = relations.stream().filter(conditionRelation -> !modifierRelationshipTypes.contains(conditionRelation.getConditionRelationType().getName())).collect(Collectors.toSet());

		if (CollectionUtils.isNotEmpty(modifierRelations)) {
			if (conditionModifiers == null) {
				conditionModifiers = new HashSet<>();
			}
			conditionModifiers.addAll(modifierRelations);
		}
		if (CollectionUtils.isNotEmpty(conditionRelationSet)) {
			if (conditionRelations == null) {
				conditionRelations = new HashSet<>();
			}
			conditionRelations.addAll(modifierRelations);
		}
	}
}
