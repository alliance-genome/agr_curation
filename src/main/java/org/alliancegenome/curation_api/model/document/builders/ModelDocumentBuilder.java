package org.alliancegenome.curation_api.model.document.builders;

import lombok.extern.slf4j.Slf4j;
import org.alliancegenome.curation_api.model.document.es.AffectedGenomicModelDocument;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ModelDocumentBuilder {


	public List<AffectedGenomicModelDocument> buildModelDocument(AffectedGenomicModel model) {
		List<AffectedGenomicModelDocument> returnList = new ArrayList<>();
		record ConditionRelationAnnotation(ConditionRelation relation, List<AGMDiseaseAnnotation> diseaseAnnotations, List<AGMPhenotypeAnnotation> phenotypeAnnotations) {};
		Map<ConditionRelation, ConditionRelationAnnotation> allConditionRels = new HashMap<>();

		model.getAgmDiseaseAnnotations().forEach(agmDiseaseAnnotation -> {
			agmDiseaseAnnotation.getConditionRelations().forEach(conditionRelation -> {
				ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(conditionRelation, k -> new ConditionRelationAnnotation(conditionRelation, new ArrayList<>(), new ArrayList<>()));
				annot.diseaseAnnotations.add(agmDiseaseAnnotation);
			});
		});

		model.getAgmPhenotypeAnnotations().forEach(agmPhenotypeAnnotation -> {
			agmPhenotypeAnnotation.getConditionRelations().forEach(conditionRelation -> {
				ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(conditionRelation, k -> new ConditionRelationAnnotation(conditionRelation, new ArrayList<>(), new ArrayList<>()));
				annot.phenotypeAnnotations.add(agmPhenotypeAnnotation);
			});
		});

		if (MapUtils.isEmpty(allConditionRels)) {
			AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
			doc.setModel(model);
			doc.setDataProvider(model.getDataProvider());
			returnList.add(doc);
			return returnList;
		}

		allConditionRels.forEach((s, conditionRelation) -> {
			AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
			doc.setModel(model);
			doc.setDataProvider(model.getDataProvider());
			doc.addConditionRelations(List.of(conditionRelation.relation));
			if (CollectionUtils.isNotEmpty(conditionRelation.phenotypeAnnotations)) {
				doc.getAssociatedPhenotype().addAll(conditionRelation.phenotypeAnnotations.stream().map(AGMPhenotypeAnnotation::getPhenotypeAnnotationObject).toList());
			}
			if (CollectionUtils.isNotEmpty(conditionRelation.diseaseAnnotations)) {
				conditionRelation.diseaseAnnotations.forEach(agmDiseaseAnnotation -> doc.addDiseaseTerm(agmDiseaseAnnotation.getDiseaseAnnotationObject()));
			}
			returnList.add(doc);
		});
		return returnList;
	}
}
