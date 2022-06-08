package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class DataProviderFmsDTO extends BaseDTO {

	private CrossReferenceFmsDTO crossReference;
	private DataProviderType type;
	
	public enum DataProviderType {
		curated, loaded;
	}
}
