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
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AnnotationDTO.class })
public class DiseaseAnnotationDTO extends AnnotationDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("do_term_curie")
	private String doTermCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("secondary_data_provider_dto")
	private DataProviderDTO secondaryDataProviderDto;

	@JsonView({ CurationView.FieldsOnly.class })
	private Boolean negated = false;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("disease_relation_name")
	private String diseaseRelationName;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("genetic_sex_name")
	private String geneticSexName;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("evidence_code_curies")
	private List<String> evidenceCodeCuries;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_identifiers")
	private List<String> diseaseGeneticModifierIdentifiers;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_relation_name")
	private String diseaseGeneticModifierRelationName;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("with_gene_identifiers")
	private List<String> withGeneIdentifiers;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("annotation_type_name")
	private String annotationTypeName;

	@JsonView({ CurationView.FieldsAndLists.class })
	@JsonProperty("disease_qualifier_names")
	private List<String> diseaseQualifierNames;

}
