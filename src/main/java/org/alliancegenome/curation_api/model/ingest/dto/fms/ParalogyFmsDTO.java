package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ParalogyFmsDTO extends BaseDTO {
	private String confidence;
	private String gene1;
	private String gene2;
	private Integer identity;
	private Integer length;
	private List<String> predictionMethodsMatched;
	private List<String> predictionMethodsNotMatched;
	private List<String> predictionMethodsNotCalled;
	private Integer rank;
	private Integer similarity;
	private Integer species;
}
