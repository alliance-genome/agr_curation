package org.alliancegenome.curation_api.constants;

import java.util.Map;

public class VariantConstants {

	public static final Map<String, Integer> SORTED_VARIANT_CONSEQUENCE_MAP = Map.ofEntries(
		Map.entry("transcript_ablation", 10),
		Map.entry("splice_acceptor_variant", 20),
		Map.entry("splice_donor_variant", 30),
		Map.entry("stop_gained", 40),
		Map.entry("frameshift_variant", 50),
		Map.entry("stop_lost", 60),
		Map.entry("start_lost", 70),
		Map.entry("transcript_amplification", 80),
		Map.entry("feature_elongation", 90),
		Map.entry("feature_truncation", 100),
		Map.entry("inframe_insertion", 110),
		Map.entry("inframe_deletion", 120),
		Map.entry("missense_variant", 130),
		Map.entry("protein_altering_variant", 140),
		Map.entry("splice_donor_5th_base_variant", 150),
		Map.entry("splice_region_variant", 160),
		Map.entry("splice_donor_region_variant", 170),
		Map.entry("splice_polypyrimidine_tract_variant", 180),
		Map.entry("incomplete_terminal_codon_variant", 190),
		Map.entry("start_retained_variant", 200),
		Map.entry("stop_retained_variant", 210),
		Map.entry("synonymous_variant", 220),
		Map.entry("coding_sequence_variant", 230),
		Map.entry("mature_miRNA_variant", 240),
		Map.entry("5_prime_UTR_variant", 250),
		Map.entry("3_prime_UTR_variant", 260),
		Map.entry("non_coding_transcript_exon_variant", 270),
		Map.entry("intron_variant", 280),
		Map.entry("NMD_transcript_variant", 290),
		Map.entry("non_coding_transcript_variant", 300),
		Map.entry("coding_transcript_variant", 310),
		Map.entry("upstream_gene_variant", 320),
		Map.entry("downstream_gene_variant", 330),
		Map.entry("TFBS_ablation", 340),
		Map.entry("TFBS_amplification", 350),
		Map.entry("TF_binding_site_variant", 360),
		Map.entry("regulatory_region_ablation", 370),
		Map.entry("regulatory_region_amplification", 380),
		Map.entry("regulatory_region_variant", 390),
		Map.entry("intergenic_variant", 400),
		Map.entry("sequence_variant", 410)
	);
}
