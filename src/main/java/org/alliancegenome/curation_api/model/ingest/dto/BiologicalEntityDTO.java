package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.model.ingest.dto.base.CurieAuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class BiologicalEntityDTO extends CurieAuditedObjectDTO {

	@JsonView({View.FieldsOnly.class})
	private String taxon;
	
}
