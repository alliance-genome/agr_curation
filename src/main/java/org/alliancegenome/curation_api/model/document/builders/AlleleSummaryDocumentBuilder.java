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
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.apache.commons.collections.CollectionUtils;

public class AlleleSummaryDocumentBuilder {

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

	private List<Map<String, String>> buildAdditionalInformation(Allele allele) {
		List<Map<String, String>> additionalInfo = new ArrayList<>();

		// Add MOD cross references
		if (CollectionUtils.isNotEmpty(allele.getCrossReferences())) {
			for (CrossReference cr : allele.getCrossReferences()) {
				Map<String, String> map = new HashMap<>();
				map.put("referencedCurie", cr.getReferencedCurie());
				map.put("url", cr.getUrlFromResourceDescriptorPage(cr.getReferencedCurie()));
				additionalInfo.add(map);
			}
		}

		// Add data provider cross reference if available
		if (allele.getDataProviderCrossReference() != null) {
			Map<String, String> map = new HashMap<>();
			map.put("referencedCurie", allele.getDataProviderCrossReference().getReferencedCurie());
			map.put("url", allele.getDataProviderCrossReference()
					.getUrlFromResourceDescriptorPage(allele.getDataProviderCrossReference().getReferencedCurie()));
			additionalInfo.add(map);
		}

		return additionalInfo;
	}
}
