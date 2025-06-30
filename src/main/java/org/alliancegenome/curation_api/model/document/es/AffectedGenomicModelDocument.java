package org.alliancegenome.curation_api.model.document.es;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections4.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonView;

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
	private List<DOTerm> diseaseTerms;
	private String dataProvider;
	private List<ConditionRelation> conditionRelations;
	private List<ConditionRelation> conditionModifiers;


	public void addDiseaseTerm(DOTerm diseaseAnnotationObject) {
		if (diseaseTerms == null) {
			diseaseTerms = new ArrayList<>();
		}
		diseaseTerms.add(diseaseAnnotationObject);
		diseaseTerms = diseaseTerms.stream()
				.distinct()
				.collect(Collectors.toList());
		Collections.sort(diseaseTerms, Comparator.comparing(o -> o.getName() == null ? "" : o.getName()));
	}

	private List<String> modifierRelationshipTypes = List.of("ameliorated_by", "exacerbated_by");

	public void addConditionRelations(Collection<ConditionRelation> relations) {
		Set<ConditionRelation> modifierRelations = relations.stream().filter(conditionRelation -> modifierRelationshipTypes.contains(conditionRelation.getConditionRelationType().getName())).collect(Collectors.toSet());
		Set<ConditionRelation> conditionRelationSet = relations.stream().filter(conditionRelation -> !modifierRelationshipTypes.contains(conditionRelation.getConditionRelationType().getName())).collect(Collectors.toSet());

		if (CollectionUtils.isNotEmpty(modifierRelations)) {
			if (conditionModifiers == null) {
				conditionModifiers = new ArrayList<>();
			}
			conditionModifiers.addAll(modifierRelations);
		}
		if (CollectionUtils.isNotEmpty(conditionRelationSet)) {
			if (conditionRelations == null) {
				conditionRelations = new ArrayList<>();
			}
			conditionRelations.addAll(conditionRelationSet);
		}
	}

	public boolean isHasDiseaseAnnotations() {
		return CollectionUtils.isNotEmpty(diseaseTerms);
	}

	public boolean isHasPhenotypeAnnotations() {
		return CollectionUtils.isNotEmpty(associatedPhenotype);
	}
}
