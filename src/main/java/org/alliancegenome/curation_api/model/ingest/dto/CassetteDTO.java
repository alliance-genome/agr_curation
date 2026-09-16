package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.CassetteComponentSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.CassetteUseSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SCRUM-6535: ingest form of Cassette.
 *
 * Carries no association DTOs. The three cassette association types and the construct cassette
 * association are submitted through their own ingest sets and files, as construct genomic entity
 * associations are, so only the components and uses that belong to the cassette itself appear here.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ReagentDTO.class, CassetteComponentSlotAnnotationDTO.class, CassetteUseSlotAnnotationDTO.class }, submitted = true)
public class CassetteDTO extends ReagentDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("cassette_symbol_dto")
	private NameSlotAnnotationDTO cassetteSymbolDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("cassette_full_name_dto")
	private NameSlotAnnotationDTO cassetteFullNameDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cassette_synonym_dtos")
	private List<NameSlotAnnotationDTO> cassetteSynonymDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cassette_component_dtos")
	private List<CassetteComponentSlotAnnotationDTO> cassetteComponentDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cassette_use_dtos")
	private List<CassetteUseSlotAnnotationDTO> cassetteUseDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;
}
