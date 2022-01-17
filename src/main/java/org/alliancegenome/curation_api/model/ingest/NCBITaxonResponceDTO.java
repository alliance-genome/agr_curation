package org.alliancegenome.curation_api.model.ingest;

import java.util.HashMap;

import groovy.transform.ToString;
import lombok.Data;

@Data
@ToString
public class NCBITaxonResponceDTO {
    private HashMap<String, Object> header;
    private HashMap<String, Object> result;
}
