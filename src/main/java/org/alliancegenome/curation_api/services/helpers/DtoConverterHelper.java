package org.alliancegenome.curation_api.services.helpers;

import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AffectedGenomicModelFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.AlleleFmsDTO;
import org.alliancegenome.curation_api.model.ingest.fms.dto.GeneFmsDTO;

import java.util.List;

import static java.util.stream.Collectors.toList;

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
