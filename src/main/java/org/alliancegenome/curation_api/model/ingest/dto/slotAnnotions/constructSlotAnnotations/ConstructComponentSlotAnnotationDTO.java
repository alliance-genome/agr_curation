package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class, NoteDTO.class })
public class ConstructComponentSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("component_symbol")
	private String componentSymbol;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("taxon_curie")
	private String taxonCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("taxon_text")
	private String taxonText;
	
}
