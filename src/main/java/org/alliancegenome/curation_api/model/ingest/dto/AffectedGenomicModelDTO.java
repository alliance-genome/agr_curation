package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.NameSlotAnnotationDTO;
import org.alliancegenome.curation_api.model.ingest.dto.slotAnnotions.SecondaryIdSlotAnnotationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.12.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {GenomicEntityDTO.class, NameSlotAnnotationDTO.class}, submitted = true)
public class AffectedGenomicModelDTO extends GenomicEntityDTO {
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("agm_full_name_dto")
	private NameSlotAnnotationDTO agmFullNameDto;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("subtype_name")
	private String subtypeName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("agm_synonym_dtos")
	private List<NameSlotAnnotationDTO> agmSynonymDtos;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("agm_secondary_id_dtos")
	private List<SecondaryIdSlotAnnotationDTO> agmSecondaryIdDtos;

}
