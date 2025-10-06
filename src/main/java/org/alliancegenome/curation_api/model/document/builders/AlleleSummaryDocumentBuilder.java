package org.alliancegenome.curation_api.model.document.builders;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlleleSummaryDocumentBuilder {


	public AlleleSummaryDocument buildSummaryDocument(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {
		AlleleSummaryDocument doc = new AlleleSummaryDocument();

		doc.setAllele(allele);

		doc.setCrossReference(getCrossReference(allele, resourceDescriptorPageService));

		doc.setDescription(buildDescription(allele));

		return doc;
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

	private CrossReference getCrossReference(Allele allele, ResourceDescriptorPageService resourceDescriptorPageService) {

		CrossReference alleleRefsCrossRef = new CrossReference();

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

		return alleleRefsCrossRef;
	}
}
