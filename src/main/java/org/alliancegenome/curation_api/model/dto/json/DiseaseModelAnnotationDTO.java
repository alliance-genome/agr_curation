package org.alliancegenome.curation_api.model.dto.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DiseaseModelAnnotationDTO {

	private String objectId;
	@JsonProperty("DOid")
	private String doid;
	private String objectName;
	
}
