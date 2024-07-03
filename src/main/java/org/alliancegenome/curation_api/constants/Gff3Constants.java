package org.alliancegenome.curation_api.constants;

import java.util.List;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Gff3Constants {
	public static final List<String> TRANSCRIPT_TYPES = List.of(
		"mRNA", "ncRNA", "piRNA", "lincRNA", "miRNA", "pre_miRNA", "snoRNA", "lncRNA",
		"tRNA", "snRNA", "rRNA", "antisense_RNA", "C_gene_segment", "V_gene_segment",
		"pseudogene_attribute", "snoRNA_gene", "pseudogenic_transcript", "lnc_RNA"
	);

}