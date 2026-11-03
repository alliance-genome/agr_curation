package org.alliancegenome.curation_api.model.ingest.dto.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

/** SCRUM-6543: ingest form of the association between two compatible transgenic tools. */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class }, submitted = true)
public class TransgenicToolTransgenicToolAssociationDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("transgenic_tool_subject_identifier")
	private String transgenicToolSubjectIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("transgenic_tool_object_identifier")
	private String transgenicToolObjectIdentifier;

}
