package org.alliancegenome.curation_api.model.dto.json;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class DataProviderDTO extends BaseDTO {

	private CrossReferenceDTO crossReference;
	private DataProviderType type;
	
	
	public enum DataProviderType {
		curated, loaded;
	}
}
