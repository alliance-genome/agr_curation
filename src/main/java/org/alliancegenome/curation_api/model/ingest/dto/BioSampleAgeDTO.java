package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SCRUM-6715: ingest form of BioSampleAge, currently only used to carry the
 * htp_expression_sample_age of an HTPExpressionDatasetSampleAnnotationDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, TemporalContextDTO.class })
public class BioSampleAgeDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("stage_dto")
	private TemporalContextDTO stageDto;

	@JsonView({ CurationView.FieldsOnly.class })
	private String age;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("when_expressed_stage_name")
	private String whenExpressedStageName;

}
