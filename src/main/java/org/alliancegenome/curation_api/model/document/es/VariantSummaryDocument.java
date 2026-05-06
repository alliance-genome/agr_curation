package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView({CurationView.VariantSummaryDocument.class})
public class VariantSummaryDocument extends AVSParentDocument {

	{
		category = "variant_summary";
		alterationType = "variant";
		alterationTypeSortOrder = 4;
	}
	private List<Variant> variantList;
	private HashSet<String> geneSynonyms;
	private HashSet<String> geneCrossReferences;
	private HashSet<String> geneSystematicNames;

	public void removeTransportFields() {
		geneSynonyms = null;
		geneCrossReferences = null;
		geneSystematicNames = null;
		dereferenceGeneCrossReferences();
	}

	private void dereferenceGeneCrossReferences() {
		if (variantList == null) {
			return;
		}
		for (Variant variant : variantList) {
			if (variant == null || variant.getCuratedVariantGenomicLocations() == null) {
				continue;
			}
			variant.getCuratedVariantGenomicLocations().forEach(cvgla -> {
				if (cvgla == null || cvgla.getPredictedVariantConsequences() == null) {
					return;
				}
				cvgla.getPredictedVariantConsequences().forEach(pvc -> {
					if (pvc == null || pvc.getVariantTranscript() == null
							|| pvc.getVariantTranscript().getTranscriptGeneAssociations() == null) {
						return;
					}
					pvc.getVariantTranscript().getTranscriptGeneAssociations().forEach(tga -> {
						Gene gene = tga.getTranscriptGeneAssociationObject();
						if (gene != null) {
							gene.setCrossReferences(null);
						}
					});
				});
			});
		}
	}
}
