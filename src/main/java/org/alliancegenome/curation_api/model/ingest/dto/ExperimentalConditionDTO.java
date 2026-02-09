package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObjectDTO.class })
public class ExperimentalConditionDTO extends AuditedObjectDTO {

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_class_curie")
	private String conditionClassCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_id_curie")
	private String conditionIdCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_quantity")
	private String conditionQuantity;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_gene_ontology_curie")
	private String conditionGeneOntologyCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_anatomy_curie")
	private String conditionAnatomyCurie;

	@JsonProperty("condition_taxon_curie")
	private String conditionTaxonCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_chemical_curie")
	private String conditionChemicalCurie;

	@JsonView({ CurationView.FieldsOnly.class })
	@JsonProperty("condition_free_text")
	private String conditionFreeText;
}
