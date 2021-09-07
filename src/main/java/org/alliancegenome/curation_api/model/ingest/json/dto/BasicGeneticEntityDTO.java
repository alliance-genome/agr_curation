package org.alliancegenome.curation_api.model.ingest.json.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Synonym;

import static java.util.stream.Collectors.toList;

@Data
public class BasicGeneticEntityDTO extends BaseDTO {
    private String primaryId;
    private String taxonId;
    private List<String> synonyms;
    private List<String> secondaryIds;
    private List<CrossReferenceDTO> crossReferences;
    private List<GenomeLocationsDTO> genomeLocations;

}
