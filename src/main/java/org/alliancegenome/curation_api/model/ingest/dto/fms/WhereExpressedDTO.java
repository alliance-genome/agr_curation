package org.alliancegenome.curation_api.model.ingest.dto.fms;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WhereExpressedDTO extends BaseDTO {
	private String whereExpressedStatement;
	private String anatomicalStructureTermId;
	private String cellularComponentTermId;
	private List<UberonSlimTermDTO> anatomicalStructureUberonSlimTermIds;
}
