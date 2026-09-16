package org.alliancegenome.curation_api.model.ingest.dto.associations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** SCRUM-6535: ingest form of the association between a cassette and a sequence targeting reagent component. */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociationDTO.class, NoteDTO.class }, submitted = true)
public class CassetteStrAssociationDTO extends EvidenceAssociationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("cassette_identifier")
	private String cassetteIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("sequence_targeting_reagent_identifier")
	private String sequenceTargetingReagentIdentifier;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;

}
