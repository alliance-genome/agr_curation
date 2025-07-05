package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.document.es.AffectedGenomicModelDocument;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.SequenceTargetingReagentGeneAssociation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModelDocumentBuilder {

	/**
	 * Consolidation logic per affected genomic model:
	 * Each unique condition relation (uniqueExperimentConditionId) will be consolidated into a single document
	 * each gene from the association list or inferred / asserted genes of disease annotation and phenotype annotation
	 * gets a separate document
	 * a model can have an associated gene via STR or allele association
	 * models without associated genes or inferred / asserted genes from disease annotations and phenotype annotations are not
	 * included (only gene-related models are needed for now)
	 */
	public List<AffectedGenomicModelDocument> buildModelDocument(AffectedGenomicModel model) {
		List<AffectedGenomicModelDocument> returnList = new ArrayList<>();
		record ConditionRelationAnnotation(List<ConditionRelation> relation, List<AGMDiseaseAnnotation> diseaseAnnotations, List<AGMPhenotypeAnnotation> phenotypeAnnotations) {
		}

		// get all associated genes from associated STRs and alleles
		List<Gene> associatedGenes = getAssociatedGenes(model);

		Map<String, ConditionRelationAnnotation> allConditionRels = new HashMap<>();
		Map<String, Set<Gene>> geneConditionMap = new HashMap<>();

		model.getAgmDiseaseAnnotations().forEach(agmDiseaseAnnotation -> {
			String uniqueConditionRelations = agmDiseaseAnnotation.getConditionRelations().stream().map(ConditionRelation::getUniqueExperimentConditionId).reduce((a, b) -> a + "," + b).orElse("");
			ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(uniqueConditionRelations, k -> new ConditionRelationAnnotation(agmDiseaseAnnotation.getConditionRelations(), new ArrayList<>(), new ArrayList<>()));
			annot.diseaseAnnotations.add(agmDiseaseAnnotation);
			Set<Gene> genes = geneConditionMap.computeIfAbsent(uniqueConditionRelations, k -> new HashSet<>());
			if (agmDiseaseAnnotation.getInferredGene() != null) {
				genes.add(agmDiseaseAnnotation.getInferredGene());
			}
			if (agmDiseaseAnnotation.getAssertedGenes() != null) {
				genes.addAll(agmDiseaseAnnotation.getAssertedGenes());
			}
			genes.addAll(associatedGenes);
		});

		model.getAgmPhenotypeAnnotations().forEach(agmPhenotypeAnnotation -> {
			String uniqueConditionRelations = agmPhenotypeAnnotation.getConditionRelations().stream().map(ConditionRelation::getUniqueExperimentConditionId).reduce((a, b) -> a + "," + b).orElse("");
			ConditionRelationAnnotation annot = allConditionRels.computeIfAbsent(uniqueConditionRelations, k -> new ConditionRelationAnnotation(agmPhenotypeAnnotation.getConditionRelations(), new ArrayList<>(), new ArrayList<>()));
			annot.phenotypeAnnotations.add(agmPhenotypeAnnotation);
			Set<Gene> genes = geneConditionMap.computeIfAbsent(uniqueConditionRelations, k -> new HashSet<>());
			if (agmPhenotypeAnnotation.getInferredGene() != null) {
				genes.add(agmPhenotypeAnnotation.getInferredGene());
			}
			if (agmPhenotypeAnnotation.getAssertedGenes() != null) {
				genes.addAll(agmPhenotypeAnnotation.getAssertedGenes());
			}
			genes.addAll(associatedGenes);
		});

		if (MapUtils.isEmpty(allConditionRels)) {
			associatedGenes.forEach(gene -> {
				AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
				doc.setModel(model);
				doc.setDataProvider(model.getDataProvider().getAbbreviation());
				doc.setGene(gene);
				returnList.add(doc);
			});
			return returnList;
		}

		allConditionRels.forEach((relation, conditionRelation) -> {
			geneConditionMap.get(relation).forEach(gene -> {
				AffectedGenomicModelDocument doc = new AffectedGenomicModelDocument();
				doc.setModel(model);
				doc.setDataProvider(model.getDataProvider().getAbbreviation());
				doc.addConditionRelations(conditionRelation.relation);
				if (CollectionUtils.isNotEmpty(conditionRelation.phenotypeAnnotations)) {
					doc.addAssociatedPhenotype(conditionRelation.phenotypeAnnotations.stream().map(AGMPhenotypeAnnotation::getPhenotypeAnnotationObject).sorted().toList());
				}
				if (CollectionUtils.isNotEmpty(conditionRelation.diseaseAnnotations)) {
					conditionRelation.diseaseAnnotations.forEach(agmDiseaseAnnotation -> {
						doc.addDiseaseTerm(agmDiseaseAnnotation.getDiseaseAnnotationObject());
					});
				}
				doc.setGene(gene);
				returnList.add(doc);
			});
		});
		return returnList;
	}

	@NotNull
	private static List<Gene> getAssociatedGenes(AffectedGenomicModel model) {
		Set<Gene> associatedGenes = new HashSet<>(model.getAgmSequenceTargetingReagentAssociations().stream()
				.map(association -> association.getAgmSequenceTargetingReagentAssociationObject().getSequenceTargetingReagentGeneAssociations()
						.stream().map(SequenceTargetingReagentGeneAssociation::getSequenceTargetingReagentGeneAssociationObject).toList())
				.flatMap(Collection::stream)
				.toList());
		associatedGenes.addAll(model.getComponents().stream()
				.map(association -> association.getAgmAlleleAssociationObject().getAlleleGeneAssociations()
						.stream().map(AlleleGeneAssociation::getAlleleGeneAssociationObject).toList())
				.flatMap(Collection::stream)
				.toList()
		);
		return new ArrayList<>(associatedGenes);
	}
}
