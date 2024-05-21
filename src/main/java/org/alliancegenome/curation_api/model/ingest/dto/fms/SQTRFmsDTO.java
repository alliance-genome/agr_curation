package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SQTRFmsDTO extends BaseDTO {
	private String id;
	private String name;
	private String taxonId;
	private String[] synonyms;
	private String[] secondaryIds;
	private String[] targetGeneIds;
	private List<CrossReferenceFmsDTO> modCrossReference;
}
