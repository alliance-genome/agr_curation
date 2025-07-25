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
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
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

		doc.setId(allele.getPrimaryExternalId());

		if (allele.getTaxon() != null) {
			doc.setSpecies(allele.getTaxon().getName());
		}

		if (allele.getAlleleSymbol() != null) {
			doc.setSymbol(allele.getAlleleSymbol().getDisplayText());
		}

		doc.setAlterationType(determineAlterationType(allele));

		doc.setSynonyms(buildSynonyms(allele));

		doc.setDescription(buildDescription(allele));

		doc.setAdditionalInformation(buildAdditionalInformation(allele, resourceDescriptorPageService));

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

	private List<String> buildSynonyms(Allele allele) {
		if (CollectionUtils.isEmpty(allele.getAlleleSynonyms())) {
			return new ArrayList<>();
		}

		List<String> alleleSynonyms;

		alleleSynonyms = allele.getAlleleSynonyms()
			.stream()
			.map(AlleleSynonymSlotAnnotation::getDisplayText)
			.collect(Collectors.toList());

		return alleleSynonyms;
	}

	//TODO: may need to update after DQM decision
	private String buildDescription(Allele allele) {

		String description = new String();

		if (CollectionUtils.isNotEmpty(allele.getRelatedNotes())) {
			Note alleleNote = allele.getRelatedNotes().get(0);
			VocabularyTerm noteType = alleleNote.getNoteType();

			if (noteType.getName().equals("mutation_description")) {
				description = alleleNote.getFreeText();
			}

		}

		return description;

	}

	private Map<String, Object> buildAdditionalInformation(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {
		Map<String, Object> additionalInfo = new HashMap<>();

		CrossReference crossRef = allele.getDataProviderCrossReference();
		if (crossRef == null && CollectionUtils.isNotEmpty(allele.getCrossReferences())) {
			crossRef = allele.getCrossReferences().get(0);
		}

		if (crossRef != null) {
			String referencedCurie = crossRef.getReferencedCurie();
			String primaryUrl = crossRef.getUrlFromResourceDescriptorPage(referencedCurie);

			Map<String, String> references = new HashMap<>();
			String referencesUrl = buildUrlFromResourceDescriptorPage(referencedCurie, "allele/references", resourceDescriptorPageService);
			references.put("crossRefCompleteUrl", referencesUrl);
			references.put("name", referencedCurie);
			additionalInfo.put("references", references);

			Map<String, String> primary = new HashMap<>();
			primary.put("crossRefCompleteUrl", primaryUrl);
			primary.put("name", referencedCurie);
			additionalInfo.put("primary", primary);
		}

		return additionalInfo;
	}


	private Map<String, String> buildAlleleOfGene(Allele allele) {
		Map<String, String> alleleOfGene = new HashMap<>();

		if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
			AlleleGeneAssociation firstAssociation = allele.getAlleleGeneAssociations().get(0);
			Gene associatedGene = firstAssociation.getAlleleGeneAssociationObject();

			if (associatedGene != null) {
				if (associatedGene.getGeneSymbol() != null) {
					String geneSymbol = associatedGene.getGeneSymbol().getDisplayText();
					alleleOfGene.put("geneSymbol", geneSymbol);
				}

				if (associatedGene.getPrimaryExternalId() != null) {
					String primaryExternalId = associatedGene.getPrimaryExternalId();
					alleleOfGene.put("primaryExternalId", primaryExternalId);
				}
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
