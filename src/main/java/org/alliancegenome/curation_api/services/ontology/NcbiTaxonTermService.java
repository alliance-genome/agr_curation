package org.alliancegenome.curation_api.services.ontology;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;

@RequestScoped
public class NcbiTaxonTermService extends BaseOntologyTermService<NCBITaxonTerm, NcbiTaxonTermDAO> {

    @Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;

    @Override
    @PostConstruct
    protected void init() {
        setSQLDao(ncbiTaxonTermDAO);
    }
    
    private static String eUtilsUrlPrefix = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?db=taxonomy&retmode=json&id=";
    
    public NCBITaxonTerm getTaxonByCurie(String taxonCurie) {
        Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:(\\d+)$");
        Matcher taxonIdMatcher = taxonIdPattern.matcher(taxonCurie);
        if (!taxonIdMatcher.find()) {
            return null;
        }
    
        NCBITaxonTerm taxon = ncbiTaxonTermDAO.find(taxonCurie);
        if (taxon == null) {
            URL eUtilsUrl = null;
            try {
                eUtilsUrl = new URL(eUtilsUrlPrefix + taxonIdMatcher.group(1));
                InputStream is = eUtilsUrl.openStream();
                JsonReader jr = Json.createReader(is);
                JsonObject json = jr.readObject();
                jr.close();
                JsonObject result = json.getJsonObject("result").getJsonObject(taxonIdMatcher.group(1));
                if (result == null) {
                    return null;
                }
                
                String name = result.getString("scientificname");
                taxon = new NCBITaxonTerm();
                taxon.setName(name);
                taxon.setCurie(taxonCurie);
                if (result.getString("status").equals("active")) {
                    taxon.setObsolete(false);
                } else {
                    taxon.setObsolete(true);
                }
                ncbiTaxonTermDAO.persist(taxon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return taxon;
    }
    
    
}
