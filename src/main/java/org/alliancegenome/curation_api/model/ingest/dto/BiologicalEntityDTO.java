package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.base.dto.CurieAuditedObjectDTO;

import lombok.Data;

@Data
public class BiologicalEntityDTO extends CurieAuditedObjectDTO {

	private String taxon;
	
}
