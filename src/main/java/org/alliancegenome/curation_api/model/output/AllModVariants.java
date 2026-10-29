package org.alliancegenome.curation_api.model.output;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * SCRUM-6757: a variant of an allele as the HGVS export lists it, one per allele - variant pair.
 *
 * @param alleleId the allele's primary external ID, e.g. ZFIN:ZDB-ALT-130411-947
 * @param variantCurie the variant's AGRKB curie; variants carry no primary external ID
 * @param hgvsG the genomic HGVS of the variant's curated location, e.g. NC_133188.1:g.33167969A>G
 * @param hgvsC the coding HGVS of its predicted consequences, one per transcript, each with its
 *   transcript accession, e.g. NM_131184.2:c.408-2A>G
 * @param hgvsP the protein HGVS of its predicted consequences, for the transcripts that have one
 * @param predictedVariantConsequences the VEP result for each transcript, ordered by transcript
 */
@JsonView({ CurationView.AllModVariantsView.class })
public record AllModVariants(String alleleId, String variantCurie, String hgvsG, List<String> hgvsC, List<String> hgvsP, List<PredictedVariantConsequence> predictedVariantConsequences) {
}
