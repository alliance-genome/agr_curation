package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneExpressionFmsDTO extends BaseDTO {
	private String geneId;
	private String assay;
	private String dateAssigned;
	private PublicationFmsDTO evidence;
	private CrossReferenceFmsDTO crossReference;
	private WhenExpressedFmsDTO whenExpressed;
	private WhereExpressedFmsDTO whereExpressed;
}
