package org.alliancegenome.curation_api.model.ingest.dto.associations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class }, submitted = true)
public class AgmAlleleAssociationDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("agm_subject_identifier")
	private String agmSubjectIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("allele_identifier")
	private String alleleIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("zygosity_curie")
	private String zygosityCurie;

}
