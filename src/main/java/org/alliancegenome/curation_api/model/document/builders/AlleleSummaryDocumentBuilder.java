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

		Allele alleleForDocument = new Allele();
		alleleForDocument.setId(allele.getId());
		alleleForDocument.setPrimaryExternalId(allele.getPrimaryExternalId());
		alleleForDocument.setTaxon(allele.getTaxon());
		alleleForDocument.setAlleleSymbol(allele.getAlleleSymbol());
		alleleForDocument.setAlleleSynonyms(allele.getAlleleSynonyms());
		alleleForDocument.setDataProvider(allele.getDataProvider());

		// Set original dataProviderCrossReference
		alleleForDocument.setDataProviderCrossReference(allele.getDataProviderCrossReference());

		// Create cross-references list with both general allele and references pages for additional info section
		List<CrossReference> crossReferences = buildEnhancedCrossReferences(allele, resourceDescriptorPageService);
		alleleForDocument.setCrossReferences(crossReferences);

		doc.setAllele(alleleForDocument);

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

	private List<CrossReference> buildEnhancedCrossReferences(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {
		List<CrossReference> crossRefs = new ArrayList<>();

		// Create a new cross-reference for allele/references using the
		// allele/references page template
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("name", "allele/references");

			SearchResponse<ResourceDescriptorPage> alleleReferencesPages = resourceDescriptorPageService.findByParams(null,
					params);
			if (alleleReferencesPages != null && !alleleReferencesPages.getResults().isEmpty()) {
				CrossReference alleleRefsCrossRef = new CrossReference();
				alleleRefsCrossRef.setReferencedCurie(allele.getDataProviderCrossReference().getReferencedCurie());
				alleleRefsCrossRef.setDisplayName(allele.getDataProviderCrossReference().getDisplayName());
				alleleRefsCrossRef.setResourceDescriptorPage(alleleReferencesPages.getResults().get(0));

				crossRefs.add(alleleRefsCrossRef);
			}
		} catch (Exception e) {
			log.warn("Could not create allele/references cross-reference for allele {}: {}",
					allele.getPrimaryExternalId(), e.getMessage());
		}

		return crossRefs;
	}
}
