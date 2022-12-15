package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AGRCurationSchemaVersion(min = "1.4.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class, ConditionRelationDTO.class, NoteDTO.class })
public class DiseaseAnnotationDTO extends AuditedObjectDTO {

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("mod_entity_id")
	private String modEntityId;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("do_term_curie")
	private String doTermCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("data_provider_name")
	private String dataProviderName;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("secondary_data_provider_name")
	private String secondaryDataProviderName;

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

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("condition_relation_dtos")
	private List<ConditionRelationDTO> conditionRelationDtos;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_curie")
	private String diseaseGeneticModifierCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("disease_genetic_modifier_relation_name")
	private String diseaseGeneticModifierRelationName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("with_gene_curies")
	private List<String> withGeneCuries;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("reference_curie")
	private String referenceCurie;

	@JsonView({ View.FieldsOnly.class })
	@JsonProperty("annotation_type_name")
	private String annotationTypeName;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("disease_qualifier_names")
	private List<String> diseaseQualifierNames;

	@JsonView({ View.FieldsAndLists.class })
	@JsonProperty("note_dtos")
	private List<NoteDTO> noteDtos;
}
