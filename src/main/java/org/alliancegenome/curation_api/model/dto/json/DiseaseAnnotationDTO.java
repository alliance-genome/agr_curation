package org.alliancegenome.curation_api.model.dto.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import java.util.Date;
import java.util.List;

@Data
public class DiseaseAnnotationDTO extends BaseDTO {

	private String objectId;
	private String objectName;
	private Date dateAssigned;
	private List<DataProviderDTO> dataProvider;
	private EvidenceDTO evidence;
	private ObjectRelationDTO objectRelation;
	private List<ConditionRelationDTO> conditionRelations;
	private List<String> primaryGeneticEntityIDs;
	@JsonProperty("DOid")
	private String doId;

}
