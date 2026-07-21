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
@AGRCurationSchemaVersion(min = "2.11.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ReagentDTO.class, CrossReferenceDTO.class }, submitted = true)
public class AntibodyDTO extends ReagentDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("name")
	private String name;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("clonality_name")
	private String clonalityName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("heavy_chain_isotype_name")
	private String heavyChainIsotypeName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("light_chain_isotype_name")
	private String lightChainIsotypeName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("antigen_taxon_curie")
	private String antigenTaxonCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("taxon_curie")
	private String taxonCurie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("antibody_target_gene_identifiers")
	private List<String> antibodyTargetGeneIdentifiers;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("reference_curies")
	private List<String> referenceCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("original_reference_curie")
	private String originalReferenceCurie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cross_reference_dtos")
	private List<CrossReferenceDTO> crossReferenceDtos;
}
