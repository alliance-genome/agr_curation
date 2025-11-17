package org.alliancegenome.curation_api.model.document.builders;

import java.util.List;

import org.alliancegenome.curation_api.model.document.es.VariantSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Variant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VariantDocumentBuilder {

	public List<VariantSummaryDTO> buildVariantDocument(Variant variant) {
		return variant.getCuratedVariantGenomicLocations().stream()
				.map(curatedVariantGenomicLocationAssociation -> {
					VariantSummaryDTO dto = new VariantSummaryDTO();
					dto.setVariant(curatedVariantGenomicLocationAssociation);
					return dto;
				}).toList();
	}
}
