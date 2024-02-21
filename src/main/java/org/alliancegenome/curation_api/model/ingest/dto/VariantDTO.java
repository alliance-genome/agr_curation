package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { GenomicEntityDTO.class, NoteDTO.class }, submitted = true)
public class VariantDTO extends GenomicEntityDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("variant_type_curie")
	private String variantTypeCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("variant_status_name")
	private String variantStatusName;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("source_general_consequence_curie")
	private String sourceGeneralConsequenceCurie;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;

}
