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
 * SCRUM-6715: ingest form of HTPExpressionDatasetSampleAnnotation, for direct DQM LinkML
 * submission (high_throughput_expression_dataset_sample_annotation_ingest_set), alongside the
 * existing FMS path (HTPExpressionDatasetSampleAnnotationFmsDTO). Extends AuditedObjectDTO, not
 * SubmittedObjectDTO - the entity has no primaryExternalId/modInternalId, matching the sibling
 * HTPExpressionDatasetAnnotationDTO's own design decision (SCRUM-6714).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, ExternalDataBaseEntityDTO.class, BioSampleAgeDTO.class,
	AnatomicalSiteDTO.class, BioSampleGenomicInformationDTO.class, NoteDTO.class, MicroarraySampleDetailsDTO.class, AnalysisDTO.class, DataProviderDTO.class }, submitted = true)
public class HTPExpressionDatasetSampleAnnotationDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	private String curie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("htp_expression_sample_dto")
	private ExternalDataBaseEntityDTO htpExpressionSampleDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("htp_expression_sample_title")
	private String htpExpressionSampleTitle;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("htp_expression_sample_type_curie")
	private String htpExpressionSampleTypeCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("expression_assay_curie")
	private String expressionAssayUsedCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("htp_expression_sample_age_dto")
	private BioSampleAgeDTO htpExpressionSampleAgeDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("htp_expression_sample_location_dtos")
	private List<AnatomicalSiteDTO> htpExpressionSampleLocationDtos;

	@JsonView({ CurationView.FieldsOnly.class })
	private String abundance;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("genomic_information_dto")
	private BioSampleGenomicInformationDTO genomicInformationDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("taxon_curie")
	private String taxonCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("genetic_sex")
	private String geneticSex;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("sequencing_format")
	private String sequencingFormat;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("assembly_versions")
	private List<String> assemblyVersions;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("related_note_dtos")
	private List<NoteDTO> relatedNoteDtos;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("dataset_ids")
	private List<String> datasetIds;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("microarray_sample_details_dto")
	private MicroarraySampleDetailsDTO microarraySampleDetailsDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("data_provider_dto")
	private DataProviderDTO dataProviderDto;

	@JsonView({ CurationView.FieldsOnly.class })
	private String symbol;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("analysis_dto")
	private AnalysisDTO analysisDto;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("biological_reference_dto")
	private ExternalDataBaseEntityDTO biologicalReferenceDto;

}
