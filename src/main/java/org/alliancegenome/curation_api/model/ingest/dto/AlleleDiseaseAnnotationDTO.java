package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={DiseaseAnnotationDTO.class}, submitted=true)
public class AlleleDiseaseAnnotationDTO extends DiseaseAnnotationDTO {
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("allele_curie")
	private String alleleCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("inferred_gene_curie")
	private String inferredGeneCurie;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("asserted_gene_curies")
	private List<String> assertedGeneCuries;
	
}
