package org.alliancegenome.curation_api.model.output;

import java.util.List;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * SCRUM-6757: the VEP result for one transcript of a variant in the HGVS export, as a PredictedVariantConsequence
 * holds it.
 *
 * @param transcript the transcript's ID, e.g. NM_201090.2
 * @param molecularConsequences the names of its VEP consequence SO terms, e.g. stop_gained, intron_variant
 * @param impact the VEP impact: HIGH, MODERATE, LOW or MODIFIER
 * @param hgvsC the coding HGVS on the transcript
 * @param hgvsP the protein HGVS, for a coding transcript
 * @param siftPrediction the SIFT prediction, e.g. deleterious
 * @param siftScore the SIFT score
 * @param polyphenPrediction the PolyPhen prediction, e.g. probably_damaging
 * @param polyphenScore the PolyPhen score
 * @param aminoAcidReference the reference amino acid(s)
 * @param aminoAcidVariant the variant amino acid(s)
 * @param codonReference the reference codon
 * @param codonVariant the variant codon
 * @param cdnaStart the start on the cDNA
 * @param cdnaEnd the end on the cDNA
 * @param cdsStart the start on the CDS
 * @param cdsEnd the end on the CDS
 * @param proteinStart the start on the protein
 * @param proteinEnd the end on the protein
 * @param geneLevelConsequence whether the consequence is for the gene rather than the transcript alone
 */
@JsonView({ CurationView.VariantHgvsView.class })
public record VariantTranscriptConsequence(
	String transcript,
	List<String> molecularConsequences,
	String impact,
	String hgvsC,
	String hgvsP,
	String siftPrediction,
	Float siftScore,
	String polyphenPrediction,
	Float polyphenScore,
	String aminoAcidReference,
	String aminoAcidVariant,
	String codonReference,
	String codonVariant,
	Integer cdnaStart,
	Integer cdnaEnd,
	Integer cdsStart,
	Integer cdsEnd,
	Integer proteinStart,
	Integer proteinEnd,
	Boolean geneLevelConsequence) {
}
