package org.alliancegenome.curation_api.model.document.es;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.collections4.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
	private List<AGMDiseaseAnnotation> diseaseAnnotations;
	private List<DOTermAssociation> diseaseModels;
	private String dataProvider;
	private List<ConditionRelation> conditionRelations;
	private List<ConditionRelation> conditionModifiers;

	public void addDiseaseAnnotation(AGMDiseaseAnnotation diseaseAnnotation) {
		if (diseaseModels == null) {
			diseaseModels = new ArrayList<>();
		}
		DOTermAssociation diseaseModel = new DOTermAssociation(
				diseaseAnnotation.getDiseaseAnnotationObject(),
				diseaseAnnotation.getNegated() ? "IS_NOT_MODEL_OF" : "IS_MODEL_OF",
				diseaseAnnotation.getNegated() ? "does not model " + diseaseAnnotation.getDiseaseAnnotationObject().getName() : diseaseAnnotation.getDiseaseAnnotationObject().getName()
		);
		diseaseModels.add(diseaseModel);
		// group by disease name and annotation negated boolean to remove duplicates
		Map<DOTerm, Map<String, List<DOTermAssociation>>> diseaseModelMap = diseaseModels.stream()
				.collect(Collectors.groupingBy(DOTermAssociation::getDisease,
						Collectors.groupingBy(DOTermAssociation::getAssociationType)));
		// pick the first annotation for each disease term and negated state
		diseaseModels = diseaseModelMap.values().stream().map(stringListMap -> {
			List<DOTermAssociation> annotations = stringListMap.values().stream()
					.flatMap(List::stream)
					.toList();
			return annotations.get(0);
		}).collect(Collectors.toList());
		Collections.sort(diseaseModels, Comparator.comparing(o -> o.getDisease().getName() == null ? "" : o.getDisease().getName().toLowerCase()));
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
		return CollectionUtils.isNotEmpty(diseaseAnnotations);
	}

	public boolean isHasDiseaseAndPhenotypeAnnotations() {
		return CollectionUtils.isNotEmpty(diseaseAnnotations) && CollectionUtils.isNotEmpty(associatedPhenotype);
	}

	public boolean isHasPhenotypeAnnotations() {
		return CollectionUtils.isNotEmpty(associatedPhenotype);
	}

	public void addAssociatedPhenotype(List<String> phenotypeList) {
		if (associatedPhenotype == null) {
			associatedPhenotype = new ArrayList<>();
		}
		associatedPhenotype.addAll(phenotypeList);
		associatedPhenotype = associatedPhenotype.stream()
				.distinct()
				.collect(Collectors.toList());
		Collections.sort(associatedPhenotype, Comparator.comparing(String::toLowerCase));
	}

	@Getter
	@Setter
	private static class DOTermAssociation {
		@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.ModelDocumentView.class})
		private DOTerm disease;
		@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.ModelDocumentView.class})
		private String associationType;
		@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.ModelDocumentView.class})
		private String diseaseModel;

		public DOTermAssociation() {
		}

		public DOTermAssociation(DOTerm disease, String associationType, String diseaseModel) {
			this.disease = disease;
			this.associationType = associationType;
			this.diseaseModel = diseaseModel;
		}

	}
}
