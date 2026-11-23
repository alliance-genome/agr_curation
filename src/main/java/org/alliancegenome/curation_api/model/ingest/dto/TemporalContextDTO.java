package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SCRUM-6715: ingest form of TemporalContext, currently only used to carry the "stage" of a
 * BioSampleAgeDTO on an HTPExpressionDatasetSampleAnnotationDTO. temporal_qualifiers is omitted -
 * no vocabulary is defined for it yet anywhere in the codebase (the FMS path doesn't populate it
 * either), so there's nothing to validate it against.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class TemporalContextDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("developmental_stage_start_curie")
	private String developmentalStageStartCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("developmental_stage_stop_curie")
	private String developmentalStageStopCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	private String age;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("stage_uberon_slim_terms")
	private List<String> stageUberonSlimTerms;

}
