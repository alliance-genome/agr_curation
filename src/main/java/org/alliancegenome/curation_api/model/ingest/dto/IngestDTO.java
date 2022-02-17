package org.alliancegenome.curation_api.model.ingest.dto;

import java.util.List;

import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Setter
@Getter
public class IngestDTO {
    
    @JsonView({View.FieldsAndLists.class})
    @JsonProperty("disease_agm_ingest_set")
    private List<AGMDiseaseAnnotationDTO> diseaseAgmIngestSet;
    
    @JsonView({View.FieldsAndLists.class})
    @JsonProperty("disease_allele_ingest_set")
    private List<AlleleDiseaseAnnotationDTO> diseaseAlleleIngestSet;

    @JsonView({View.FieldsAndLists.class})
    @JsonProperty("disease_gene_ingest_set")
    private List<GeneDiseaseAnnotationDTO> diseaseGeneIngestSet;
    
}
