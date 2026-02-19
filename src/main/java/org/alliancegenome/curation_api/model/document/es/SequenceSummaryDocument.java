package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView({ CurationView.SequenceSummaryDocument.class })
public class SequenceSummaryDocument extends ESDocument {

	{
		category = "sequence_summary";
	}

	private Allele allele;
	private CuratedVariantGenomicLocationAssociation variant;
	private PredictedVariantConsequence consequence;

}
