package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataProviderFmsDTO extends BaseDTO {

	private CrossReferenceFmsDTO crossReference;
	private DataProviderType type;

	public enum DataProviderType {
		curated, loaded;
	}
}
