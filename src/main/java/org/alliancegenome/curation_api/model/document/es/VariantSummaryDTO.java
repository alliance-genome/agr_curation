package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@JsonView(View.VariantDetailView.class)
public class VariantSummaryDTO {

	private CuratedVariantGenomicLocationAssociation variant;

	private Allele allele;

}
