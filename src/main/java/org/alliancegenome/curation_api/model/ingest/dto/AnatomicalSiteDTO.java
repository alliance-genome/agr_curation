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

/**
 * SCRUM-6715: ingest form of AnatomicalSite, currently only used to carry the
 * htp_expression_sample_locations of an HTPExpressionDatasetSampleAnnotationDTO. AnatomicalSite is
 * also used by GeneExpressionAnnotation's own where-expressed data, but that entity has no direct
 * LinkML submission path today, so this DTO is scoped to the sample annotation's own needs.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "2.19.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class AnatomicalSiteDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("anatomical_structure_curie")
	private String anatomicalStructureCurie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("anatomical_structure_qualifier_curies")
	private List<String> anatomicalStructureQualifierCuries;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("anatomical_structure_uberon_term_curies")
	private List<String> anatomicalStructureUberonTermCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("anatomical_structure_uberon_term_other")
	private Boolean anatomicalStructureUberonTermOther;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("anatomical_substructure_curie")
	private String anatomicalSubstructureCurie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("anatomical_substructure_qualifier_curies")
	private List<String> anatomicalSubstructureQualifierCuries;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("anatomical_substructure_uberon_term_curies")
	private List<String> anatomicalSubstructureUberonTermCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("anatomical_sub_structure_uberon_term_other")
	private Boolean anatomicalSubStructureUberonTermOther;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("cellular_component_curie")
	private String cellularComponentCurie;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cellular_component_ribbon_term_curies")
	private List<String> cellularComponentRibbonTermCuries;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("cellular_component_qualifier_curies")
	private List<String> cellularComponentQualifierCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("cellular_component_other")
	private Boolean cellularComponentOther;

}
