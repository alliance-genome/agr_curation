package org.alliancegenome.curation_api.model.document.builders;

import lombok.extern.slf4j.Slf4j;
import org.alliancegenome.curation_api.model.document.es.ModelDocument;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;

import java.util.Collection;
import java.util.List;

@Slf4j
public class ModelDocumentBuilder {


	public ModelDocument buildModelDocument(AffectedGenomicModel model) {
		ModelDocument doc = new ModelDocument();
		doc.setModel(model);
		model.getAgmPhenotypeAnnotations()
			.forEach(phenotypeAnnotation -> {
				if (phenotypeAnnotation != null) {
					doc.getAssociatedPhenotype().add(phenotypeAnnotation.getPhenotypeAnnotationObject());
				}
			});
		if (model.getAgmDiseaseAnnotations() != null) {
			model.getAgmDiseaseAnnotations().forEach(diseaseAnnotation -> {
				doc.addDiseaseTerms(diseaseAnnotation.getDiseaseAnnotationObject());
			});
			List<ConditionRelation> relations = model.getAgmDiseaseAnnotations().stream()
				.map(Annotation::getConditionRelations)
				.flatMap(Collection::stream).toList();
			doc.addConditionRelations(relations);
		}
		if (model.getAgmPhenotypeAnnotations() != null) {
			model.getAgmPhenotypeAnnotations()
				.forEach(phenotypeAnnotation -> {
					if (phenotypeAnnotation != null) {
						doc.addConditionRelations(phenotypeAnnotation.getConditionRelations());
					}
				});
		}
		doc.setDataProvider(model.getDataProvider());
		return doc;
	}

}
