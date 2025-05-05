package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WhereExpressedFmsDTO extends BaseDTO {
	private String whereExpressedStatement;
	private String cellularComponentTermId;
	private String cellularComponentQualifierTermId;
	private String anatomicalStructureTermId;
	private String anatomicalStructureQualifierTermId;
	private String anatomicalSubStructureTermId;
	private String anatomicalSubStructureQualifierTermId;
	private List<UberonSlimTermDTO> anatomicalStructureUberonSlimTermIds;
	private List<UberonSlimTermDTO> anatomicalSubStructureUberonSlimTermIds;
}
