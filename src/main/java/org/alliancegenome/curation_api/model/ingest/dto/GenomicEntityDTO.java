package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.3.3", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={BiologicalEntityDTO.class})
public class GenomicEntityDTO extends BiologicalEntityDTO {

	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("synonym_dtos")
	private List<SynonymDTO> synonymDtos;
	
}
