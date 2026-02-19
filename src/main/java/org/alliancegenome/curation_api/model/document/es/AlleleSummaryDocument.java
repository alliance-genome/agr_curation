package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@JsonView(CurationView.AlleleSummaryDocument.class)
public class AlleleSummaryDocument extends ESDocument {

	{
		category = "allele_summary";
	}

	private Allele allele;
	private String alterationType;
	private Integer alterationTypeSortOrder;
	private String description;
	private Map<String, Object> additionalInformation;
	private Gene alleleOfGene;
	private CrossReference crossReference;
	private List<Variant> variants;
	private HashSet<String> geneIds;
	private Boolean hasPhenotype;
	private Boolean hasDisease;
	

	public void setAlleleOfGene(Gene alleleOfGene) {
		this.alleleOfGene = alleleOfGene;
		if (geneIds == null) {
			geneIds = new HashSet<>();
		}
		geneIds.add(alleleOfGene.getPrimaryExternalId());
	}
}

