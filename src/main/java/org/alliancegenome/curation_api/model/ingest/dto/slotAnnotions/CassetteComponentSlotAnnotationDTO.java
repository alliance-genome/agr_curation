package org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** SCRUM-6535: ingest form of a cassette component named only by symbol. */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotationDTO.class, NoteDTO.class })
public class CassetteComponentSlotAnnotationDTO extends SlotAnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("component_symbol")
	private String componentSymbol;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("taxon_curie")
	private String taxonCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("taxon_text")
	private String taxonText;

}
