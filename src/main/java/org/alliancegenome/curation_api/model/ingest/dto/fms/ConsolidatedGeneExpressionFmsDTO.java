package org.alliancegenome.curation_api.model.ingest.dto.fms;


import lombok.Data;
import lombok.EqualsAndHashCode;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConsolidatedGeneExpressionFmsDTO extends BaseDTO {
	private String geneId;
	private String assay;
	private String dateAssigned;
	private PublicationFmsDTO evidence;
	private List<CrossReferenceFmsDTO> crossReferences;
	private WhenExpressedFmsDTO whenExpressed;
	private WhereExpressedFmsDTO whereExpressed;
}
