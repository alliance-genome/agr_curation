package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotationDTO.class }, submitted = true)
public class GeneDiseaseAnnotationDTO extends DiseaseAnnotationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_identifier")
	private String geneIdentifier;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("sgd_strain_background_identifier")
	private String sgdStrainBackgroundIdentifier;

}
