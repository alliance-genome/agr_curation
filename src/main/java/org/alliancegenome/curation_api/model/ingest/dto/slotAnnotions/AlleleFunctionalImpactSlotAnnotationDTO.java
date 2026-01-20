package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class AlleleFunctionalImpactSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("functional_impact_names")
	private List<String> functionalImpactNames;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("phenotype_term_curie")
	private String phenotypeTermCurie;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("phenotype_statement")
	private String phenotypeStatement;

}
