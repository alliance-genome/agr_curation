package org.alliancegenome.curation_api.model.ingest.dto.associations.alleleAssociations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AlleleGenomicEntityAssociationDTO.class })
public class AlleleGeneAssociationDTO extends AlleleGenomicEntityAssociationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("gene_curie")
	private String geneCurie;

}
