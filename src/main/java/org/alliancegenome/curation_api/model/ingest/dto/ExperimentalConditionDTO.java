package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.ingest.dto.base.AuditedObjectDTO;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObjectDTO.class})
public class ExperimentalConditionDTO extends AuditedObjectDTO{

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_class_curie")
	private String conditionClassCurie;

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_id_curie")
	private String conditionIdCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_quantity")
	private String conditionQuantity;
	

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_gene_ontology_curie")
	private String conditionGeneOntologyCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_anatomy_curie")
	private String conditionAnatomyCurie;
	
	@JsonProperty("condition_taxon_curie")
	private String conditionTaxonCurie;
	

	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_chemical_curie")
	private String conditionChemicalCurie;
	
	@JsonView({View.FieldsOnly.class})
	@JsonProperty("condition_free_text")
	private String conditionFreeText;
}
