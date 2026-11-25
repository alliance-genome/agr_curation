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
 * SCRUM-6714: ingest form of HTPExpressionDatasetAnnotation, for direct DQM LinkML submission
 * (high_throughput_expression_dataset_annotation_ingest_set), alongside the existing FMS path
 * (HTPExpressionDatasetAnnotationFmsDTO). Extends AuditedObjectDTO, not SubmittedObjectDTO - the
 * entity has no primaryExternalId/modInternalId.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, ExternalDataBaseEntityDTO.class, NoteDTO.class, DataProviderDTO.class, AnalysisDTO.class }, submitted = true)
public class HTPExpressionDatasetAnnotationDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	private String curie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("htp_expression_dataset_dto")
	private ExternalDataBaseEntityDTO htpExpressionDatasetDto;

	@JsonView({ CurationView.FieldsOnly.class })
	private String name;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("note_dto")
	private NoteDTO relatedNoteDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("number_of_channels")
	private Integer numberOfChannels;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("sub_series")
	private List<String> subSeries;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("category_tag_names")
	private List<String> categoryTagNames;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("data_provider_dto")
	private DataProviderDTO dataProviderDto;

	@JsonView({ CurationView.FieldsOnly.class })
	private String symbol;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("analysis_dto")
	private AnalysisDTO analysisDto;

}
