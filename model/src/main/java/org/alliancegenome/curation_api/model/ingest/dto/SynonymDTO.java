package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObjectDTO.class})
public class SynonymDTO extends AuditedObjectDTO {

	@JsonView({View.FieldsOnly.class})
	private String name;
	
}
