package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.UniqueIdAuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class DiseaseAnnotationDTO extends UniqueIdAuditedObjectDTO {

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("mod_entity_id")
	private String modEntityId;

	@JsonView({View.FieldsOnly.class})
	private String subject;

	@JsonView({View.FieldsOnly.class})
	private String object;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("data_provider")
	private String dataProvider;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("secondary_data_provider")
	private String secondaryDataProvider;

	@JsonView({View.FieldsOnly.class})
	private Boolean negated = false;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("predicate")
	private String diseaseRelation;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("genetic_sex")
	private String geneticSex;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("evidence_codes")
	private List<String> evidenceCodes;

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("condition_relations")
	private List<ConditionRelationDTO> conditionRelations;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("disease_genetic_modifier")
	private String diseaseGeneticModifier;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("disease_genetic_modifier_relation")
	private String diseaseGeneticModifierRelation;

	@JsonView({View.FieldsAndLists.class})
	private List<String> with;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("single_reference")
	private String singleReference;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("disease_annotation_summary")
	private String diseaseAnnotationSummary;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("disease_annotation_note")
	private String diseaseAnnotationNote;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("table_id")
	protected Long tableId;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("annotation_type")
	private String annotationType;

	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("disease_qualifiers")
	private List<String> diseaseQualifiers;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("sgd_strain_background")
	private String sgdStrainBackground;
	
	@JsonView({View.FieldsAndLists.class})
	@JsonProperty("related_notes")
	private List<NoteDTO> relatedNotes;
}
