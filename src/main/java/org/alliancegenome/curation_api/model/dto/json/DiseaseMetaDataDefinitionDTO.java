package org.alliancegenome.curation_api.model.dto.json;

import java.util.List;

import lombok.Data;

@Data
public class DiseaseMetaDataDefinitionDTO {

	private MetaDataDTO metaData;
	private List<DiseaseModelAnnotationDTO> data;
}
