package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
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

		Optional<Gene> optionalAlleleOfGene = buildAlleleOfGene(allele);
		optionalAlleleOfGene.ifPresent(gene -> doc.setAlleleOfGene(gene));

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

		String description = "";

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

	private Optional<Gene> buildAlleleOfGene(Allele allele) {
		List<AlleleGeneAssociation> alleleGeneAssociations = allele.getAlleleGeneAssociations();

		if (CollectionUtils.isEmpty(alleleGeneAssociations)) {
			return Optional.empty();
		}

		List<Gene> alleleOfGeneList = alleleGeneAssociations.stream()
			.filter(aga -> aga.getRelation().getName().equals("is_allele_of"))
			.filter(aga -> !aga.getInternal() && !aga.getObsolete())
			.map(aga -> aga.getAlleleGeneAssociationObject())
			.collect(Collectors.toList());

		return alleleOfGeneList.stream().findFirst();
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

		CrossReference alleleRefsCrossRef = new CrossReference();

		try {
			if (allele.getDataProvider() == null) {
				return alleleRefsCrossRef;
			}

			String dataProviderAbbreviation = allele.getDataProvider().getAbbreviation();
			ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(dataProviderAbbreviation, "allele/references");

			if (page != null && allele.getDataProviderCrossReference() != null) {
				alleleRefsCrossRef.setReferencedCurie(allele.getDataProviderCrossReference().getReferencedCurie());
				alleleRefsCrossRef.setDisplayName(allele.getDataProviderCrossReference().getDisplayName());
				alleleRefsCrossRef.setResourceDescriptorPage(page);
			}
		} catch (Exception e) {
			log.warn("Could not create allele/references cross-reference for allele {}: {}", allele.getPrimaryExternalId(), e.getMessage());
		}

		return alleleRefsCrossRef;
	}
}
