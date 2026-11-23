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

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, CrossReferenceDTO.class })
public class ExternalDataBaseEntityDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	private String curie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("secondary_identifiers")
	private List<String> secondaryIdentifiers;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("preferred_cross_reference_dto")
	private CrossReferenceDTO preferredCrossReferenceDto;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cross_reference_dtos")
	private List<CrossReferenceDTO> crossReferenceDtos;

}
