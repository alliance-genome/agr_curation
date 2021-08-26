package org.alliancegenome.curation_api.services;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.*;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.*;
import org.alliancegenome.curation_api.model.ingest.json.dto.*;
import org.alliancegenome.curation_api.model.input.Pagination;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class GeneService extends BaseService<Gene, GeneDAO> {

    @Inject GeneDAO geneDAO;
    @Inject CrossReferenceDAO crossReferenceDAO;
    @Inject CrossReferenceService crossReferenceService;


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
        if(gene != null) {
            gene.getSynonyms().size();
            gene.getSecondaryIdentifiers().size();
        }
        return gene;
    }

    @Transactional
    public void processUpdate(GeneDTO gene) {

        Gene g = geneDAO.find(gene.getBasicGeneticEntity().getPrimaryId());
        boolean newGene = false;

        if(g == null ) {
            g = new Gene();
            g.setCurie(gene.getBasicGeneticEntity().getPrimaryId());
            newGene = true;
        }

        g.setGeneSynopsis(gene.getGeneSynopsis());
        g.setGeneSynopsisURL(gene.getGeneSynopsisUrl());

        g.setSymbol(gene.getSymbol());
        g.setName(gene.getName());
        g.setTaxon(gene.getBasicGeneticEntity().getTaxonId());
        g.setType(gene.getSoTermId());

        List<CrossReferenceDTO> incomingCrossReferences = gene.getBasicGeneticEntity().getCrossReferences();
        List<CrossReference> persitentCrossReferences = new ArrayList<>();
        for(CrossReferenceDTO crossReferenceDTO : incomingCrossReferences){
            CrossReference crossReference = crossReferenceService.processUpdate(crossReferenceDTO);
            persitentCrossReferences.add(crossReference);
        }

        g.setCrossReferences(persitentCrossReferences);

        Set<String> secondaryIds = new LinkedHashSet<>(g.getSecondaryIdentifiers());
        secondaryIds.addAll(gene.getBasicGeneticEntity().getSecondaryIds());
        g.setSecondaryIdentifiers(new ArrayList<>(secondaryIds));

        if(newGene) {
            create(g);
        }else {
            update(g);
        }

    }

}
