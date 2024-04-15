package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BiologicalEntityDTO.class, CrossReferenceDTO.class })
public class GenomicEntityDTO extends BiologicalEntityDTO {

	@JsonProperty("cross_reference_dtos")
	private List<CrossReferenceDTO> crossReferenceDtos;
	
}
