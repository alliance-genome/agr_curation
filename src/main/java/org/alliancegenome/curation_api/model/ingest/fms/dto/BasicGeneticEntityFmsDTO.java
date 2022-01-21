package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class BasicGeneticEntityFmsDTO extends BaseDTO {
    private String primaryId;
    private String taxonId;
    private List<String> synonyms;
    private List<String> secondaryIds;
    private List<CrossReferenceFmsDTO> crossReferences;
    private List<GenomeLocationsFmsDTO> genomeLocations;

}
