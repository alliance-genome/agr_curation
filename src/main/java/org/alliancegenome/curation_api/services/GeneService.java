package org.alliancegenome.curation_api.services;

import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.base.SearchResults;
import org.alliancegenome.curation_api.dao.CrossReferenceDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.ingest.json.dto.CrossReferenceDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.GeneDTO;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JBossLog
@RequestScoped
public class GeneService extends BaseService<Gene, GeneDAO> {

    @Inject
    GeneDAO geneDAO;
    @Inject
    CrossReferenceDAO crossReferenceDAO;
    @Inject
    CrossReferenceService crossReferenceService;
    @Inject
    SynonymService synonymService;


    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(geneDAO);
    }

    public SearchResults<Gene> getAllGenes(Pagination pagination) {
        return getAll(pagination);
    }

    @Transactional
    public Gene getByIdOrCurie(String id) {
        Gene gene = geneDAO.getByIdOrCurie(id);
        if (gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneDTO gene) {

        Gene g = geneDAO.find(gene.getBasicGeneticEntity().getPrimaryId());
        boolean newGene = false;

        if (g == null) {
            g = new Gene();
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
            handleNewSynonyms(gene, g);
            newGene = true;
        } else {
            handleUpdateSynonyms(gene, g);
        }

        g.setGeneSynopsis(gene.getGeneSynopsis());
        g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());

        g.setSymbol(gene.getSymbol());
        g.setName(gene.getName());
        g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
        g.setType(gene.getSoTermId());

        List<CrossReferenceDTO> incomingCrossReferences = gene.getBasicGeneticEntity().getCrossReferences();
        List<CrossReference> persitentCrossReferences = new ArrayList<>();
        for (CrossReferenceDTO crossReferenceDTO : incomingCrossReferences) {
            CrossReference crossReference = crossReferenceService.processUpdate(crossReferenceDTO);
            persitentCrossReferences.add(crossReference);
        }

        g.setCrossReferences(persitentCrossReferences);
        g.setSecondaryIdentifiers(gene.getBasicGeneticEntity().getSecondaryIds());

        if (newGene) {
            create(g);
        } else {
            update(g);
        }

    }

    private void handleNewSynonyms(GeneDTO gene, Gene g) {
        if (CollectionUtils.isNotEmpty(gene.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> synonyms = DtoConverter.getSynonyms(gene);
            synonyms.forEach(synonym -> synonymService.create(synonym));
            g.setSynonyms(synonyms);
        }
    }

    private void handleUpdateSynonyms(GeneDTO geneDTO, Gene gene) {
        if (CollectionUtils.isNotEmpty(geneDTO.getBasicGeneticEntity().getSynonyms())) {
            List<Synonym> newSynonyms = DtoConverter.getSynonyms(geneDTO);

            List<Synonym> existingSynonyms = gene.getSynonyms();

            // remove synonyms that are not found in the new synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                List<Long> removeSynIDs = existingSynonyms.stream()
                        .filter(synonym -> !existingSynonymStrings.contains(synonym.getName()))
                        .map(Synonym::getId)
                        .collect(Collectors.toList());
                removeSynIDs.forEach(id -> synonymService.delete(Long.toString(id)));
                existingSynonyms.removeIf(synonym -> newSynonyms.stream().noneMatch(synonym1 -> synonym1.getName().equals(synonym.getName())));
            }
            // add new synonyms that are not found in the existing synonym list
            if (CollectionUtils.isNotEmpty(existingSynonyms)) {
                List<String> existingSynonymStrings = existingSynonyms.stream().map(Synonym::getName).collect(Collectors.toList());
                final List<Synonym> newCollect = newSynonyms.stream().filter(synonym -> !existingSynonymStrings.contains(synonym.getName())).collect(Collectors.toList());
                newCollect.forEach(synonym -> {
                    synonym.setGenomicEntityList(List.of(gene));
                    synonymService.create(synonym);
                });
                existingSynonyms.addAll(newCollect);
            }
        }
    }


}
