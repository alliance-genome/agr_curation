package org.alliancegenome.curation_api.model.ingest.fms.dto;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseDTO;

import lombok.Data;

@Data
public class MoleculeFmsDTO extends BaseDTO {
    private String id;
    private String name;
    private String inchi;
    private String inchikey;
    private String iupac;
    private String formula;
    private String smiles;
    private List<String> synonyms;
    private List<CrossReferenceFmsDTO> crossReferences;
}
