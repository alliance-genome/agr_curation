package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class DtoConverter {

    public static List<Synonym> getSynonyms(GeneDTO gene) {
        if (gene.getBasicGeneticEntity().getSynonyms() == null)
            return null;
        return gene.getBasicGeneticEntity().getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }
}
