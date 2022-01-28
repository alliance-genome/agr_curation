package org.alliancegenome.curation_api.services.helpers;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.fms.dto.*;

public class DtoConverterHelper {

    public static List<Synonym> getSynonyms(GeneFmsDTO gene) {
        if (gene.getBasicGeneticEntity().getSynonyms() == null)
            return null;
        return gene.getBasicGeneticEntity().getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }

    public static List<Synonym> getSynonyms(AffectedGenomicModelFmsDTO agm) {
        if (agm.getSynonyms() == null)
            return null;
        return agm.getSynonyms().stream()
                .map(s -> {
                    Synonym syn = new Synonym();
                    syn.setName(s);
                    return syn;
                }).collect(toList());
    }

    public static List<Synonym> getSynonyms(AlleleFmsDTO allele) {
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
