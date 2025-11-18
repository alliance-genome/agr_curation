package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alliancegenome.curation_api.model.document.es.VariantSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VariantDocumentBuilder {

	public List<VariantSummaryDTO> buildVariantDocument(Variant variant) {
		return variant.getCuratedVariantGenomicLocations().stream()
				.map(curatedVariantGenomicLocationAssociation -> {
					List<VariantSummaryDTO> dtos = new ArrayList<>();
					variant.getAlleleVariantAssociations().forEach(alleleVariantAssociation -> {
						VariantSummaryDTO dto = new VariantSummaryDTO();
						dto.setVariant(curatedVariantGenomicLocationAssociation);
						Allele alleleAssociationSubject = alleleVariantAssociation.getAlleleAssociationSubject();
						// create a shallow version of an allele for performance / resource purposes
						if (alleleAssociationSubject != null) {
							Allele allele = new Allele();
							allele.setPrimaryExternalId(alleleAssociationSubject.getPrimaryExternalId());
							dto.setAllele(allele);
						}
						dtos.add(dto);
					});
					return dtos;
				}).flatMap(Collection::stream)
				.toList();
	}
}
