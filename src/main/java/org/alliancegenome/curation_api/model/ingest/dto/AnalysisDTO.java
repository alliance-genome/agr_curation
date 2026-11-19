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
 * SCRUM-6714: ingest form of Analysis, currently only used to carry FB scRNA-seq analysis
 * metadata on an HTPExpressionDatasetAnnotationDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, NoteDTO.class, CrossReferenceDTO.class })
public class AnalysisDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("analysis_type_curie")
	private String analysisTypeCurie;

	// Plain abbreviation, not a DataProviderDTO wrapper - see HTPExpressionDatasetAnnotationDTO's
	// own data_provider field for why.
	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("data_provider")
	private String dataProvider;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("data_provider_cross_reference_dto")
	private CrossReferenceDTO dataProviderCrossReferenceDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("related_note_dtos")
	private List<NoteDTO> relatedNoteDtos;

}
