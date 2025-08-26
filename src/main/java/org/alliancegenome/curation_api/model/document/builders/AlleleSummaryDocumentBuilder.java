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
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
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

		if (allele.getTaxon() != null) {
			doc.setSpecies(allele.getTaxon().getName());
		}

		if (allele.getAlleleSymbol() != null) {
			doc.setSymbol(allele.getAlleleSymbol().getDisplayText());
		}

		doc.setAllele(alleleForDocument);

		doc.setAlterationType(determineAlterationType(allele));

		doc.setDescription(buildDescription(allele));

		doc.setAlleleOfGene(buildAlleleOfGene(allele));

		doc.setConstructs(buildConstructs(allele, resourceDescriptorPageService));

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

	private List<Map<String, Object>> buildConstructs(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {
		List<Map<String, Object>> constructs = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(allele.getAlleleConstructAssociations())) {
			for (AlleleConstructAssociation association : allele.getAlleleConstructAssociations()) {
				Construct construct = association.getAlleleConstructAssociationObject();


				if (construct != null) {
					Map<String, Object> constructMap = new HashMap<>();

					if (construct.getPrimaryExternalId() != null) {
						constructMap.put("id", construct.getPrimaryExternalId());
					}

					String displayText = null;
					if (construct.getConstructSymbol() != null && construct.getConstructSymbol().getDisplayText() != null) {
						displayText = construct.getConstructSymbol().getDisplayText();
					} else if (construct.getConstructFullName() != null && construct.getConstructFullName().getDisplayText() != null) {
						displayText = construct.getConstructFullName().getDisplayText();
					} else if (construct.getPrimaryExternalId() != null) {
						displayText = construct.getPrimaryExternalId();
					}

					if (displayText != null) {
						constructMap.put("name", displayText);
					}

					constructMap.put("type", "construct");

					Map<String, Object> crossReferenceMap = new HashMap<>();
					if (construct.getDataProviderCrossReference() != null || construct.getPrimaryExternalId() != null) {
						Map<String, String> primary = new HashMap<>();

						String referencedCurie = construct.getPrimaryExternalId();
						if (construct.getDataProviderCrossReference() != null && construct.getDataProviderCrossReference().getReferencedCurie() != null) {
							referencedCurie = construct.getDataProviderCrossReference().getReferencedCurie();
						}

						if (referencedCurie != null) {
							primary.put("name", referencedCurie);

							String constructUrl = buildUrlFromResourceDescriptorPage(referencedCurie, "construct", resourceDescriptorPageService);
							if (constructUrl != null) {
								primary.put("crossRefCompleteUrl", constructUrl);
							}

							crossReferenceMap.put("primary", primary);
						}
					}
					constructMap.put("crossReferenceMap", crossReferenceMap);

					constructs.add(constructMap);
				}
			}
		}

		return constructs;
	}

	private String buildUrlFromResourceDescriptorPage(String referencedCurie, String pageName, ResourceDescriptorPageService resourceDescriptorPageService) {
		String[] parts = referencedCurie.split(":");
		if (parts.length >= 2) {
			String prefix = parts[0];
			String localId = parts[1];

			ResourceDescriptorPage resourcePage = resourceDescriptorPageService.getPageForResourceDescriptor(prefix, pageName);
			if (resourcePage != null) {
				return resourcePage.getUrlTemplate().replace("[%s]", localId);
			}
		}
		return null;
	}
}
