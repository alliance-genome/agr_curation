package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.TransgenicToolUseSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SCRUM-6535: ingest form of TransgenicTool.
 *
 * Symbol, full name and synonyms reuse NameSlotAnnotationDTO: LinkML declares no dedicated DTO
 * class for them, since none adds a slot beyond NameSlotAnnotationDTO's own.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ReagentDTO.class, TransgenicToolUseSlotAnnotationDTO.class }, submitted = true)
public class TransgenicToolDTO extends ReagentDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("transgenic_tool_symbol_dto")
	private NameSlotAnnotationDTO transgenicToolSymbolDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("transgenic_tool_full_name_dto")
	private NameSlotAnnotationDTO transgenicToolFullNameDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("transgenic_tool_synonym_dtos")
	private List<NameSlotAnnotationDTO> transgenicToolSynonymDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("transgenic_tool_use_dtos")
	private List<TransgenicToolUseSlotAnnotationDTO> transgenicToolUseDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cross_reference_dtos")
	private List<CrossReferenceDTO> crossReferenceDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;
}
