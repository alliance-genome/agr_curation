package org.alliancegenome.curation_api.services.helpers;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.MoleculeDTO;

public class DtoConverterHelper {

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
    
    public static List<Synonym> getSynonyms(MoleculeDTO molecule) {
        if (molecule.getSynonyms() == null)
            return null;
        return molecule.getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }
}
