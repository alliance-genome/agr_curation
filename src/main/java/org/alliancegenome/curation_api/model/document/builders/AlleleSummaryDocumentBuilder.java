package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlleleSummaryDocumentBuilder {

	@Inject
	ResourceDescriptorPageService resourceDescriptorPageService;

	public AlleleSummaryDocument buildSummaryDocument(Allele allele) {
		AlleleSummaryDocument doc = new AlleleSummaryDocument();

		doc.setAllele(allele);

		// Species
		if (allele.getTaxon() != null) {
			doc.setSpecies(allele.getTaxon().getName());
		}

		// Symbol
		if (allele.getAlleleSymbol() != null) {
			doc.setSymbol(allele.getAlleleSymbol().getDisplayText());
		}

		// Alteration Type (Category)
		doc.setAlterationType(determineAlterationType(allele));

		// Synonyms
		if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms())) {
			doc.setSynonyms(allele.getAlleleSynonyms().stream()
					.map(AlleleSynonymSlotAnnotation::getDisplayText)
					.collect(Collectors.toList()));
		} else {
			doc.setSynonyms(new ArrayList<>());
		}

		// Description (needs to be fixed)
		if (allele.getAlleleFullName() != null) {
			doc.setDescription(allele.getAlleleFullName().getDisplayText());
		}

		// Additional Information (MOD Cross References)
		doc.setAdditionalInformation(buildAdditionalInformation(allele));

		// Allele of Gene
		if (CollectionUtils.isNotEmpty(allele.getAlleleGeneAssociations())) {
			AlleleGeneAssociation firstAssociation = allele.getAlleleGeneAssociations().get(0);
			Gene associatedGene = firstAssociation.getAlleleGeneAssociationObject();

			if (associatedGene != null && associatedGene.getGeneSymbol() != null) {
				String geneSymbol = associatedGene.getGeneSymbol().getDisplayText();
				doc.setAlleleOfGene(geneSymbol);
			}
		}

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

	private Map<String, Object> buildAdditionalInformation(Allele allele) {
		Map<String, Object> additionalInfo = new HashMap<>();

		// Determine which cross reference to use - prefer data provider cross reference
		CrossReference crossRef = allele.getDataProviderCrossReference();
		if (crossRef == null && CollectionUtils.isNotEmpty(allele.getCrossReferences())) {
			crossRef = allele.getCrossReferences().get(0);
		}

		if (crossRef != null) {
			String referencedCurie = crossRef.getReferencedCurie();
			String primaryUrl = crossRef.getUrlFromResourceDescriptorPage(referencedCurie);

			// Build references section using the allele/references resource descriptor page
			Map<String, String> references = new HashMap<>();
			String referencesUrl = buildUrlFromResourceDescriptorPage(referencedCurie, "allele/references");
			references.put("crossRefCompleteUrl", referencesUrl);
			references.put("name", referencedCurie);
			additionalInfo.put("references", references);

			// Build primary section using the default allele page
			Map<String, String> primary = new HashMap<>();
			primary.put("crossRefCompleteUrl", primaryUrl);
			primary.put("name", referencedCurie);
			additionalInfo.put("primary", primary);
		}

		return additionalInfo;
	}

	private String buildUrlFromResourceDescriptorPage(String referencedCurie, String pageName) {
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
