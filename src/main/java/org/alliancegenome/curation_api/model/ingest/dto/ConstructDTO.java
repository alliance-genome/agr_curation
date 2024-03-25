package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.constructSlotAnnotations.ConstructComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ReagentDTO.class, ConstructComponentSlotAnnotationDTO.class }, submitted = true)
public class ConstructDTO extends ReagentDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("construct_symbol_dto")
	private NameSlotAnnotationDTO constructSymbolDto;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("construct_full_name_dto")
	private NameSlotAnnotationDTO constructFullNameDto;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("construct_synonym_dtos")
	private List<NameSlotAnnotationDTO> constructSynonymDtos;
	
	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("construct_component_dtos")
	private List<ConstructComponentSlotAnnotationDTO> constructComponentDtos;
}
