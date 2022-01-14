package org.alliancegenome.curation_api.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseCrudService;
import org.alliancegenome.curation_api.dao.AlleleDAO;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleDTO;
import org.alliancegenome.curation_api.model.ingest.json.dto.AlleleObjectRelationsDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.AlleleValidator;
import org.apache.commons.collections4.CollectionUtils;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class AlleleService extends BaseCrudService<Allele, AlleleDAO> {

    @Inject AlleleDAO alleleDAO;
    @Inject AlleleValidator alleleValidator;
    @Inject GeneDAO geneDAO;
    
    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(alleleDAO);
    }
    
    @Override
    @Transactional
    public ObjectResponse<Allele> update(Allele uiEntity) {
        Allele dbEntity = alleleValidator.validateAnnotation(uiEntity);
        return new ObjectResponse<Allele>(alleleDAO.persist(dbEntity));
    }

    @Transactional
    public void processUpdate(AlleleDTO allele) {
        if (!validateAlleleDTO(allele)) {
            return;
        }
        
        Allele dbAllele = alleleDAO.find(allele.getPrimaryId());
        //log.info("Allele: " + allele + " : " + alleleDTO.getPrimaryId());
        if(dbAllele == null) {
            dbAllele = new Allele();
            dbAllele.setCurie(allele.getPrimaryId());
        }
        
        dbAllele.setSymbol(allele.getSymbol());
        dbAllele.setDescription(allele.getDescription());
        dbAllele.setTaxon(allele.getTaxonId());
        
        alleleDAO.persist(dbAllele);

    }
    
    private boolean validateAlleleDTO(AlleleDTO allele) {
        // TODO: replace regex method with DB lookup for taxon ID once taxons are loaded
        
        // Check for required fields
        if (allele.getPrimaryId() == null || allele.getSymbol() == null
                || allele.getSymbolText() == null || allele.getTaxonId() == null) {
            log.debug("Entry for allele " + allele.getPrimaryId() + " missing required fields - skipping");
            return false;
        }
        
        // Validate taxon ID
        Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:\\d+$");
        Matcher taxonIdMatcher = taxonIdPattern.matcher(allele.getTaxonId());
        if (!taxonIdMatcher.find()) {
            log.debug("Invalid taxon ID for allele " + allele.getPrimaryId() + " - skipping");
            return false;
        }
        
        // Validate allele object relations
        // TODO: validate construct relation once constructs are loaded
        if (CollectionUtils.isNotEmpty(allele.getAlleleObjectRelations())) {
            for (AlleleObjectRelationsDTO objectRelation : allele.getAlleleObjectRelations()) {
                if (objectRelation.getObjectRelation().containsKey("gene")) {
                    Gene gene = geneDAO.find(objectRelation.getObjectRelation().get("gene"));
                    if (gene == null) {
                        log.debug("Related gene not found in database for " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                    if (objectRelation.getObjectRelation().containsKey("associationType") &&
                            !objectRelation.getObjectRelation().get("associationType").equals("allele_of")) {
                        log.debug("Invalid association type for related gene of " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                }
                if (objectRelation.getObjectRelation().containsKey("construct")) {
                    if (objectRelation.getObjectRelation().containsKey("associationType") &&
                            !objectRelation.getObjectRelation().get("associationType").equals("contains")) {
                        log.debug("Invalid association type for related construct of " + allele.getPrimaryId() + " - skipping");
                        return false;
                    }
                }
            }
        }
        
        return true;
    }

}
