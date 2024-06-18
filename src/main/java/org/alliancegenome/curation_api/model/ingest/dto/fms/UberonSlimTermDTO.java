package org.alliancegenome.curation_api.model.ingest.dto.fms;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

@Data
@EqualsAndHashCode(callSuper = true)
public class UberonSlimTermDTO extends BaseDTO {
	private String uberonTerm;
}