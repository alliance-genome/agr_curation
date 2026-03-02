package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonView({ CurationView.VariantSummaryDocument.class })
public class VariantSummaryDocument extends ESDocument {

	{
		category = "variant_summary";
	}
	protected String subCategory;

	private String alterationType = "variant";
	private Integer alterationTypeSortOrder = 4;
	private Boolean hasPhenotype = false;
	private Boolean hasDisease = false;

	private Allele allele;
	private CuratedVariantGenomicLocationAssociation variant;
	private List<Variant> variants;
	private HashSet<String> geneIds;

}
