package org.alliancegenome.curation_api.services.helpers;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.AffectedGenomicModelDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleDTO;

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
    
    public static List<Synonym> getSynonyms(AffectedGenomicModelDTO agm) {
        if (agm.getSynonyms() == null)
            return null;
        return agm.getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }
    
    public static List<Synonym> getSynonyms(AlleleDTO allele) {
        if (allele.getSynonyms() == null)
            return null;
        return allele.getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }
}
