package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;

@Data
public class EvidenceFmsDTO extends BaseDTO {

	private PublicationFmsDTO publication;
	private List<String> evidenceCodes;

}
