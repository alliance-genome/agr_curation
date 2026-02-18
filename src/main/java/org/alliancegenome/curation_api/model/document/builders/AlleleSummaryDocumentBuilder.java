package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlleleSummaryDocumentBuilder {

	public AlleleSummaryDocument buildSummaryDocument(AlleleSummaryDTO alleleDTO, ResourceDescriptorPageService resourceDescriptorPageService) {
		Allele allele = alleleDTO.getAllele();
		AlleleSummaryDocument doc = new AlleleSummaryDocument();

		doc.setAllele(allele);

		doc.setCrossReference(getCrossReference(allele, resourceDescriptorPageService));

		setAlterationType(alleleDTO.getVariantCount(), doc);
		Optional<Gene> optionalAlleleOfGene = buildAlleleOfGene(allele);
		optionalAlleleOfGene.ifPresent(doc::setAlleleOfGene);

		doc.setVariants(alleleDTO.getVariants());
		doc.setHasPhenotype(alleleDTO.getHasPhenotype());
		doc.setHasDisease(alleleDTO.getHasDisease());

		return doc;
	}

	private void setAlterationType(Long variantCount, AlleleSummaryDocument doc) {
		if (variantCount == null || variantCount == 0) {
			doc.setAlterationType("allele");
			doc.setAlterationTypeSortOrder(3);
		} else if (variantCount == 1) {
			doc.setAlterationType("allele with one variant");
			doc.setAlterationTypeSortOrder(1);
		} else {
			doc.setAlterationType("allele with multiple variants");
			doc.setAlterationTypeSortOrder(2);
		}
	}

	private Optional<Gene> buildAlleleOfGene(Allele allele) {
		List<AlleleGeneAssociation> alleleGeneAssociations = allele.getAlleleGeneAssociations();

		if (CollectionUtils.isEmpty(alleleGeneAssociations)) {
			return Optional.empty();
		}

		ArrayList<Gene> alleleOfGeneList = new ArrayList<>();

		for (AlleleGeneAssociation assoc : alleleGeneAssociations) {
			if (assoc.isNotInternalOrObsolete()) {
				alleleOfGeneList.add(assoc.getAlleleGeneAssociationObject());
			}
		}

		return alleleOfGeneList.stream().findFirst();
	}

	private CrossReference getCrossReference(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {

		CrossReference alleleRefsCrossRef = new CrossReference();

		if (allele.getDataProvider() == null) {
			return alleleRefsCrossRef;
		}

		String dataProviderAbbreviation = allele.getDataProvider().getAbbreviation();

		// Determine the correct references page based on the current page type
		String referencesPageName = "allele/references";
		if (allele.getDataProviderCrossReference() != null && allele.getDataProviderCrossReference().getResourceDescriptorPage() != null) {
			String currentPageName = allele.getDataProviderCrossReference().getResourceDescriptorPage().getName();
			if ("transgene".equals(currentPageName)) {
				referencesPageName = "transgene/references";
			}
		}

		ResourceDescriptorPage page = resourceDescriptorPageService.getPageForResourceDescriptor(dataProviderAbbreviation, referencesPageName);

		if (page != null && allele.getDataProviderCrossReference() != null) {
			alleleRefsCrossRef.setReferencedCurie(allele.getDataProviderCrossReference().getReferencedCurie());
			alleleRefsCrossRef.setDisplayName(allele.getDataProviderCrossReference().getDisplayName());
			alleleRefsCrossRef.setResourceDescriptorPage(page);
		}

		return alleleRefsCrossRef;
	}
}
