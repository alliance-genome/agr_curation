package org.alliancegenome.curation_api.model.ingest.dto.associations.constructAssociations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.11.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociationDTO.class, NoteDTO.class }, submitted = true)
public class ConstructGenomicEntityAssociationDTO extends EvidenceAssociationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("construct_identifier")
	private String constructIdentifier;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("genomic_entity_relation_name")
	private String genomicEntityRelationName;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("genomic_entity_identifier")
	private String genomicEntityIdentifier;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;

}
