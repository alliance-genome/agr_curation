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
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AnnotationDTO.class })
public class DiseaseAnnotationDTO extends AnnotationDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("do_term_curie")
	private String doTermCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("secondary_data_provider_dto")
	private DataProviderDTO secondaryDataProviderDto;

	@JsonView({ View.FieldsOnly.class })
	private Boolean negated = false;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("disease_relation_name")
	private String diseaseRelationName;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("genetic_sex_name")
	private String geneticSexName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("evidence_code_curies")
	private List<String> evidenceCodeCuries;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_identifiers")
	private List<String> diseaseGeneticModifierIdentifiers;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_relation_name")
	private String diseaseGeneticModifierRelationName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("with_gene_identifiers")
	private List<String> withGeneIdentifiers;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("annotation_type_name")
	private String annotationTypeName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("disease_qualifier_names")
	private List<String> diseaseQualifierNames;

}
