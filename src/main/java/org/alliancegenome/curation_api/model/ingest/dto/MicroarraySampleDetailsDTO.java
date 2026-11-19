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
 * SCRUM-6715: ingest form of MicroarraySampleDetails, currently only used to carry the
 * microarray_sample_details of an HTPExpressionDatasetSampleAnnotationDTO. The LinkML schema also
 * models this class under SubmittedObject (data_provider, related_notes, curie, etc.), but the
 * current MicroarraySampleDetails entity only has channelId/channelNumber - this DTO matches the
 * entity as it exists today, not the full LinkML inheritance chain.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class MicroarraySampleDetailsDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("channel_id")
	private String channelId;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("channel_number")
	private Integer channelNumber;

}
