package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;

@Data
public class MetaDataFmsDTO extends BaseDTO {

	private String dateProduced;
	private DataProviderFmsDTO dataProvider;
	private String release;

}
