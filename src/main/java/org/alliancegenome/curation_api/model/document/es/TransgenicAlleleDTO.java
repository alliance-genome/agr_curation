package org.alliancegenome.curation_api.model.document.es;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.view.View;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
@JsonView(View.TransgenicAllelesDocument.class)
public class TransgenicAlleleDTO {

	private Allele allele;

	public Construct construct;

	private Boolean hasDiseaseAnnotations = Boolean.FALSE;

	private Boolean hasPhenotypeAnnotations = Boolean.FALSE;
}
