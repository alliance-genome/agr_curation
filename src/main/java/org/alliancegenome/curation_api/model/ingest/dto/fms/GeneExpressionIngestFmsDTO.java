package org.alliancegenome.curation_api.model.ingest.dto.fms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneExpressionIngestFmsDTO extends BaseDTO {
	private MetaDataFmsDTO metaData;
	private List<GeneExpressionFmsDTO> data;
}
