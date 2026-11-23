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
 * SCRUM-6715: ingest form of BioSampleGenomicInformation, currently only used to carry the
 * genomic_information of an HTPExpressionDatasetSampleAnnotationDTO.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class BioSampleGenomicInformationDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("biosample_allele_curie")
	private String bioSampleAlleleCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("biosample_agm_curie")
	private String bioSampleAgmCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("biosample_agm_type")
	private String bioSampleAgmType;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("biosample_text")
	private String bioSampleText;

}
