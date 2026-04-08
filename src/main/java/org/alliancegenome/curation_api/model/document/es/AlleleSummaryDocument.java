package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.AlleleSummaryDocument.class)
public class AlleleSummaryDocument extends AVSParentDocument {

	{
		category = "allele_summary";
	}

	private String description;
	private Map<String, Object> additionalInformation;
	private Gene alleleOfGene;
	private CrossReference crossReference;
	private List<Variant> variantList;
	private Set<String> diseases;
	private Set<String> diseasesWithParents;
	private Set<String> diseasesAgrSlim;
	private Set<String> constructExpressedComponents;
	private Set<String> constructRegulatoryRegions;
	private Set<String> constructKnockdownComponents;
	private Set<String> geneSynonyms;
	private Set<String> phenotypeStatements;

	public void setAlleleOfGene(Gene alleleOfGene) {
		this.alleleOfGene = alleleOfGene;
		if (geneIds == null) {
			geneIds = new HashSet<>();
		}
		geneIds.add(alleleOfGene.getPrimaryExternalId());
	}

	public void removeTransportFields() {
		diseases = null;
		diseasesWithParents = null;
		diseasesAgrSlim = null;
		constructExpressedComponents = null;
		constructRegulatoryRegions = null;
		constructKnockdownComponents = null;
		geneSynonyms = null;
		phenotypeStatements = null;
	}
}

