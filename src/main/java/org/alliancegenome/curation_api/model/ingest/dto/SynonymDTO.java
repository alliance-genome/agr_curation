package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;

import lombok.Data;

@Data
public class SynonymDTO extends AuditedObjectDTO {

	private String name;
	
}
