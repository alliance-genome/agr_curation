package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;

@Data
public class PublicationFmsDTO extends BaseDTO {

	private String publicationId;
	private CrossReferenceFmsDTO crossReference;

}
