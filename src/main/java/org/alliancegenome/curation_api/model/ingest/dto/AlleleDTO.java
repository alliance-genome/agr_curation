package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.3.3", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={GenomicEntityDTO.class}, submitted=true)
public class AlleleDTO extends GenomicEntityDTO {

	@JsonView({View.FieldsOnly.class})
	private String symbol;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inheritance_mode_name")
	private String inheritanceModeName;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("in_collection_name")
	private String inCollectionName;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("sequencing_status_name")
	private String sequencingStatusName;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("is_extinct")
	private Boolean isExtinct;

}
