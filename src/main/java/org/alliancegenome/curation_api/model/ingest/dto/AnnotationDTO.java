package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.7.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, ConditionRelationDTO.class, NoteDTO.class, DataProviderDTO.class })
public class AnnotationDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_entity_id")
	private String modEntityId;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_internal_id")
	private String modInternalId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("data_provider_dto")
	private DataProviderDTO dataProviderDto;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("condition_relation_dtos")
	private List<ConditionRelationDTO> conditionRelationDtos;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("reference_curie")
	private String referenceCurie;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;
}
