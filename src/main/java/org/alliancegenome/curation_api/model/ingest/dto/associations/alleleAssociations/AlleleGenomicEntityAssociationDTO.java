package org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.NoteDTO;
import org.alliancegenome.curation_api.model.ingest.dto.associations.EvidenceAssociationDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { EvidenceAssociationDTO.class, NoteDTO.class })
public class AlleleGenomicEntityAssociationDTO extends EvidenceAssociationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("allele_curie")
	private String alleleCurie;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("relation_name")
	private String relationName;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("evidence_code_curie")
	private String evidenceCodeCurie;
	
	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("note_dto")
	private NoteDTO noteDto;

}
