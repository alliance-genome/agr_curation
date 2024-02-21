package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrthologyFmsDTO extends BaseDTO {
	private Integer gene1Species;
	private Integer gene2Species;
	private String isBestScore;
	private String isBestRevScore;
	private String gene1;
	private String gene2;
	private List<String> predictionMethodsMatched;
	private List<String> predictionMethodsNotMatched;
	private List<String> predictionMethodsNotCalled;
	private String confidence;
	private Boolean moderateFilter;
	private Boolean strictFilter;
}
