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
		record ConditionRelationAnnotation(List<ConditionRelation> relation, List<AGMDiseaseAnnotation> diseaseAnnotations, List<AGMPhenotypeAnnotation> phenotypeAnnotations) {
		}

		Map<String, ConditionRelationAnnotation> allConditionRels = new HashMap<>();

		model.getAgmDiseaseAnnotations().forEach(agmDiseaseAnnotation -> {
			String uniqueConditionRelations = agmDiseaseAnnotation.getConditionRelations().stream().map(ConditionRelation::getUniqueExperimentConditionId).reduce((a, b) -> a + "," + b).orElse("");
			ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(uniqueConditionRelations, k -> new ConditionRelationAnnotation(agmDiseaseAnnotation.getConditionRelations(), new ArrayList<>(), new ArrayList<>()));
			annot.diseaseAnnotations.add(agmDiseaseAnnotation);
		});

		model.getAgmPhenotypeAnnotations().forEach(agmPhenotypeAnnotation -> {
			String uniqueConditionRelations = agmPhenotypeAnnotation.getConditionRelations().stream().map(ConditionRelation::getUniqueExperimentConditionId).reduce((a, b) -> a + "," + b).orElse("");
			ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(uniqueConditionRelations, k -> new ConditionRelationAnnotation(agmPhenotypeAnnotation.getConditionRelations(), new ArrayList<>(), new ArrayList<>()));
			annot.phenotypeAnnotations.add(agmPhenotypeAnnotation);
		});

		if (MapUtils.isEmpty(allConditionRels)) {
			AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
			doc.setModel(model);
			doc.setDataProvider(model.getDataProvider());
			returnList.add(doc);
			return returnList;
		}

		allConditionRels.forEach((relation, conditionRelation) -> {
			AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
			doc.setModel(model);
			doc.setDataProvider(model.getDataProvider());
			doc.addConditionRelations(conditionRelation.relation);
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
