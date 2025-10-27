package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.Allele;

import lombok.Data;

@Data
public class AlleleSummaryDTO {

	private Allele allele;
	private Long variantCount;

	public AlleleSummaryDTO(Allele allele, Long variantCount) {
		this.allele = allele;
		this.variantCount = variantCount;
	}
}
