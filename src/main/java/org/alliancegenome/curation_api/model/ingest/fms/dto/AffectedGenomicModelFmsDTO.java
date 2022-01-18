package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel.Subtype;

import lombok.Data;

@Data
public class AffectedGenomicModelFmsDTO extends BaseDTO {

    private String primaryID;
    private String name;
    private Subtype subtype;
    private String taxonId;
    private CrossReferenceFmsDTO crossReference;
    private List<String> synonyms;
    private List<String> secondaryIds;
    private List<AffectedGenomicModelComponentFmsDTO> affectedGenomicModelComponents;
    private List<String> sequenceTargetingReagentIDs;
    private List<String> parentalPopulationIDs;

}
