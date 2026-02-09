package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.11.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { DiseaseAnnotationDTO.class }, submitted = true)
public class AGMDiseaseAnnotationDTO extends DiseaseAnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("agm_identifier")
	private String agmIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("inferred_gene_identifier")
	private String inferredGeneIdentifier;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("inferred_allele_identifier")
	private String inferredAlleleIdentifier;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("asserted_gene_identifiers")
	private List<String> assertedGeneIdentifiers;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("asserted_allele_identifiers")
	private List<String> assertedAlleleIdentifiers;

}
