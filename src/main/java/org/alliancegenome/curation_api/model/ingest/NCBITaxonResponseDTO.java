package org.alliancegenome.curation_api.model.ingest;

import java.util.HashMap;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NCBITaxonResponseDTO {
	private HashMap<String, Object> header;
	private HashMap<String, Object> result;
}
