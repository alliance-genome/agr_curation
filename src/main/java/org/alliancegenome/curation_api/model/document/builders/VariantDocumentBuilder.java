package org.alliancegenome.curation_api.model.document.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.alliancegenome.curation_api.model.document.es.VariantSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.apache.commons.collections.CollectionUtils;

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
							AlleleSymbolSlotAnnotation alleleSymbolSlotAnnotation = new AlleleSymbolSlotAnnotation();
							alleleSymbolSlotAnnotation.setFormatText(alleleAssociationSubject.getAlleleSymbol().getFormatText());
							alleleSymbolSlotAnnotation.setDisplayText(alleleAssociationSubject.getAlleleSymbol().getDisplayText());
							allele.setAlleleSymbol(alleleSymbolSlotAnnotation);
							if (CollectionUtils.isNotEmpty(alleleAssociationSubject.getAlleleGeneAssociations())) {
								List<AlleleGeneAssociation> isAlleleOfList = alleleAssociationSubject.getAlleleGeneAssociations().stream()
										.filter(alleleGeneAssociation -> alleleGeneAssociation.getRelation().getName().equals("is_allele_of")).toList();
								if(CollectionUtils.isNotEmpty(isAlleleOfList)) {
									AlleleGeneAssociation isAlleleOf = isAlleleOfList.getFirst();
									Gene associatedGene = isAlleleOf.getAlleleGeneAssociationObject();
									Gene shallowVersion = new Gene();
									shallowVersion.setPrimaryExternalId(associatedGene.getPrimaryExternalId());
									GeneSymbolSlotAnnotation symbol = new GeneSymbolSlotAnnotation();
									symbol.setFormatText(associatedGene.getGeneSymbol().getFormatText());
									symbol.setDisplayText(associatedGene.getGeneSymbol().getDisplayText());
									shallowVersion.setGeneSymbol(symbol);
									dto.setIsAlleleOfGene(shallowVersion);
								}
							}
							dto.setAllele(allele);
						}
						dtos.add(dto);
					});
					return dtos;
				}).flatMap(Collection::stream)
				.toList();
	}
}
