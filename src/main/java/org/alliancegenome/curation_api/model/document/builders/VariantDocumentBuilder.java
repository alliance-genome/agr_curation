package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.alliancegenome.curation_api.model.document.es.VariantSummaryDocument;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VariantDocumentBuilder {

	public List<VariantSummaryDocument> buildVariantDocument(Variant variant) {
		return variant.getCuratedVariantGenomicLocations().stream()
				.map(curatedVariantGenomicLocationAssociation -> {
					List<VariantSummaryDocument> dtos = new ArrayList<>();
					variant.getAlleleVariantAssociations().forEach(alleleVariantAssociation -> {
						VariantSummaryDocument dto = new VariantSummaryDocument();
						dto.setVariant(curatedVariantGenomicLocationAssociation);
						Allele alleleAssociationSubject = alleleVariantAssociation.getAlleleAssociationSubject();
						dto.setAllele(alleleAssociationSubject);
						HashSet<String> geneIds = new HashSet<>();
						alleleAssociationSubject.getAlleleGeneAssociations().stream().forEach(association -> {
							geneIds.add(association.getAlleleGeneAssociationObject().getPrimaryExternalId());
						});
						dto.setGeneIds(geneIds);
						dtos.add(dto);
					});
					return dtos;
				}).flatMap(Collection::stream)
				.toList();
	}
}
