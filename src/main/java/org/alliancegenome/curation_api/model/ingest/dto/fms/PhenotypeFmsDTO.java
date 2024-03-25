package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PhenotypeFmsDTO extends BaseDTO {

	private String objectId;
	
	@JsonProperty("primaryGeneticEntityIDs")
	private List<String> primaryGeneticEntityIds;
	
	private List<PhenotypeTermIdentifierFmsDTO> phenotypeTermIdentifiers;
	
	private String phenotypeStatement;
	
	private PublicationRefFmsDTO evidence;
	
	private String dateAssigned;
	
	private List<ConditionRelationFmsDTO> conditionRelations;

}
