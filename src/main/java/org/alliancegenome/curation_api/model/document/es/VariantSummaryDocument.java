package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView(value = { CurationView.VariantDocument.class })
public class VariantSummaryDocument extends ESDocument {

	{
		category = "variant_summary";
	}
	protected String subCategory;

	private Allele allele;
	private CuratedVariantGenomicLocationAssociation variant;

}
