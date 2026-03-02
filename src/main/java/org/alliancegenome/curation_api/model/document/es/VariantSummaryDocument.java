package org.alliancegenome.curation_api.model.document.es;


import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView({ CurationView.VariantSummaryDocument.class })
public class VariantSummaryDocument extends AVSParentDocument {

	{
		category = "variant_summary";
	}
	
	protected String subCategory;
	private CuratedVariantGenomicLocationAssociation variantLocation;
}
