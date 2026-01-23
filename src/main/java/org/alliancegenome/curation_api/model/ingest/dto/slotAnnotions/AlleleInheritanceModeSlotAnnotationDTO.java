package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.3.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class })
public class AlleleInheritanceModeSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("inheritance_mode_name")
	private String inheritanceModeName;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("phenotype_term_curie")
	private String phenotypeTermCurie;
	
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("phenotype_statement")
	private String phenotypeStatement;

}
