package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class AlleleFmsDTO extends BaseDTO {

    private String primaryId;
    private String symbol;
    private String symbolText;
    private String taxonId;
    private List<String> synonyms;
    private String description;
    private String alleleDescription;
    private List<String> secondaryIds;
    private List<AlleleObjectRelationsFmsDTO> alleleObjectRelations;
    private List<CrossReferenceFmsDTO> crossReferences;
}
