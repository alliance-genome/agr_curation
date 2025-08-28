package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlleleSummaryDocumentBuilder {

	public AlleleSummaryDocument buildSummaryDocument(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {
		AlleleSummaryDocument doc = new AlleleSummaryDocument();

		doc.setAllele(allele);

		doc.setCrossReference(getCrossReference(allele, resourceDescriptorPageService));

		doc.setAlterationType(determineAlterationType(allele));

		doc.setDescription(buildDescription(allele));

		doc.setAlleleOfGene(buildAlleleOfGene(allele));

		doc.setConstructSlimList(getConstructs(allele));

		return doc;
	}

	private String determineAlterationType(Allele allele) {
		if (allele.getAlleleVariantAssociations() == null || allele.getAlleleVariantAssociations().isEmpty()) {
			return "allele";
		} else if (allele.getAlleleVariantAssociations().size() == 1) {
			return "allele with one variant";
		} else {
			return "allele with multiple variants";
		}
	}

	private String buildDescription(Allele allele) {

		String description = new String();

		if (CollectionUtils.isNotEmpty(allele.getRelatedNotes())) {
			List<String> descriptionList = allele.getRelatedNotes()
					.stream()
					.filter(note -> note.getNoteType().getName().equals("mutation_description"))
					.map(note -> note.getFreeText())
					.collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(descriptionList)) {
				description = descriptionList.get(0);
			}
		}

		return description;

	}

	private Gene buildAlleleOfGene(Allele allele) {
		Gene alleleOfGene = new Gene();
		List<AlleleGeneAssociation> alleleGeneAssociations = allele.getAlleleGeneAssociations();

		if (CollectionUtils.isNotEmpty(alleleGeneAssociations)) {

			List<Gene> alleleOfGeneList = alleleGeneAssociations.stream()
					.filter(aga -> aga.getRelation().getName().equals("is_allele_of"))
					.filter(aga -> aga.getInternal() == false && aga.getObsolete() == false)
					.map(aga -> aga.getAlleleGeneAssociationObject())
					.collect(Collectors.toList());

			if (CollectionUtils.isNotEmpty(alleleOfGeneList)) {
				alleleOfGene = alleleOfGeneList.get(0);
			}

		}

		return alleleOfGene;
	}

	private List<Construct> getConstructs(Allele allele) {
		List<Construct> constructs = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(allele.getAlleleConstructAssociations())) {
			for (AlleleConstructAssociation association : allele.getAlleleConstructAssociations()) {
				Construct construct = association.getAlleleConstructAssociationObject();

				if (construct != null) {
					constructs.add(construct);
				}
			}

		}
		return constructs;
	}

	private CrossReference getCrossReference(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {

		// Create a new cross-reference for allele/references using the allele/references page template
		CrossReference alleleRefsCrossRef = new CrossReference();

		try {
			Map<String, Object> params = new HashMap<>();
			params.put("name", "allele/references");
			params.put("resourceDescriptor.prefix", allele.getDataProvider().getAbbreviation());

			SearchResponse<ResourceDescriptorPage> alleleReferencesPages = resourceDescriptorPageService.findByParams(null, params);
			if (alleleReferencesPages != null && !alleleReferencesPages.getResults().isEmpty()) {
				alleleRefsCrossRef.setReferencedCurie(allele.getDataProviderCrossReference().getReferencedCurie());
				alleleRefsCrossRef.setDisplayName(allele.getDataProviderCrossReference().getDisplayName());
				alleleRefsCrossRef.setResourceDescriptorPage(alleleReferencesPages.getResults().get(0));

			}
		} catch (Exception e) {
			log.warn("Could not create allele/references cross-reference for allele {}: {}",
					allele.getPrimaryExternalId(), e.getMessage());
		}

		return alleleRefsCrossRef;
	}
}
