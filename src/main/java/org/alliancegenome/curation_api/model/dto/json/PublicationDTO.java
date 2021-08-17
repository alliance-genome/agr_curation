package org.alliancegenome.curation_api.model.dto.json;

import lombok.Data;
import org.alliancegenome.curation_api.base.BaseDTO;

import java.util.List;

@Data
public class PublicationDTO extends BaseDTO {

	private String publicationId;
	private CrossReferenceDTO crossReference;
}
