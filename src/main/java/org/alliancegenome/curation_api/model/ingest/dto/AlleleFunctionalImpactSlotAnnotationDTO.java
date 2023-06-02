package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class AlleleFunctionalImpactSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("functional_impact_names")
	private List<String> functionalImpactNames;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("phenotype_term_curie")
	private String phenotypeTermCurie;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("phenotype_statement")
	private String phenotypeStatement;

}
