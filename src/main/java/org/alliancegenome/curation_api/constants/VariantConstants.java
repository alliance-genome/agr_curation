package org.alliancegenome.curation_api.constants;

import java.util.Map;

public class VariantConstants {

	public static final Map<String, Integer> SORTED_VARIANT_CONSEQUENCE_MAP = Map.ofEntries(
		Map.entry("transcript_ablation", 1),
		Map.entry("splice_acceptor_variant", 2),
		Map.entry("splice_donor_variant", 3),
		Map.entry("stop_gained", 4),
		Map.entry("frameshift_variant", 5),
		Map.entry("stop_lost", 6),
		Map.entry("start_lost", 7),
		Map.entry("transcript_amplification", 8),
		Map.entry("feature_elongation", 9),
		Map.entry("feature_truncation", 10),
		Map.entry("inframe_insertion", 11),
		Map.entry("inframe_deletion", 12),
		Map.entry("missense_variant", 13),
		Map.entry("protein_altering_variant", 14),
		Map.entry("splice_donor_5th_base_variant", 15),
		Map.entry("splice_region_variant", 16),
		Map.entry("splice_donor_region_variant", 17),
		Map.entry("splice_polypyrimidine_tract_variant", 18),
		Map.entry("incomplete_terminal_codon_variant", 19),
		Map.entry("start_retained_variant", 20),
		Map.entry("stop_retained_variant", 21),
		Map.entry("synonymous_variant", 22),
		Map.entry("coding_sequence_variant", 23),
		Map.entry("mature_miRNA_variant", 24),
		Map.entry("5_prime_UTR_variant", 25),
		Map.entry("3_prime_UTR_variant", 26),
		Map.entry("non_coding_transcript_exon_variant", 27),
		Map.entry("intron_variant", 28),
		Map.entry("NMD_transcript_variant", 29),
		Map.entry("non_coding_transcript_variant", 30),
		Map.entry("coding_transcript_variant", 31),
		Map.entry("upstream_gene_variant", 32),
		Map.entry("downstream_gene_variant", 33),
		Map.entry("TFBS_ablation", 34),
		Map.entry("TFBS_amplification", 35),
		Map.entry("TF_binding_site_variant", 36),
		Map.entry("regulatory_region_ablation", 37),
		Map.entry("regulatory_region_amplification", 38),
		Map.entry("regulatory_region_variant", 39),
		Map.entry("intergenic_variant", 40),
		Map.entry("sequence_variant", 41)
	);
}
