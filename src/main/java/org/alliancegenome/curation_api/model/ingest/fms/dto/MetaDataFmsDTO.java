package org.alliancegenome.curation_api.model.ingest.fms.dto;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class MetaDataFmsDTO extends BaseDTO {
	
	private String dateProduced;
	private DataProviderFmsDTO dataProvider;
	private String release;

}
