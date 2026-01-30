package org.alliancegenome.curation_api.model.document.es;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;

import lombok.Data;

@Data
public class AlleleSummaryDTO {

	private Allele allele;
	private Long variantCount;
	private List<Variant> variants;
	private Boolean hasPhenotype;
	private Boolean hasDisease;


	public AlleleSummaryDTO(Allele allele, Long variantCount) {
		this.allele = allele;
		this.variantCount = variantCount;
	}

	public AlleleSummaryDTO(Allele allele, List<Variant> variants, Boolean hasPhenotype, Boolean hasDisease) {
		this.allele = allele;
		this.variants = variants;
		this.variantCount = variants != null ? (long) variants.size() : 0L;
		this.hasPhenotype = hasPhenotype;
		this.hasDisease = hasDisease;
	}
}
