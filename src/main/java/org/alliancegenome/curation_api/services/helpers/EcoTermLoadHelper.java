package org.alliancegenome.curation_api.services.helpers;

import java.util.*;

import org.alliancegenome.curation_api.dao.ontology.EcoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.EcoTerm;

import lombok.extern.jbosslog.JBossLog;

import javax.transaction.Transactional;

@JBossLog
public class EcoTermLoadHelper {

    private static String agr_subset = "agr_eco_terms";
    
    private static Map<String, String> abbreviationMap;
    static
    {
        abbreviationMap = new HashMap<>();
        abbreviationMap.put("ECO:0000270", "IEP");
        abbreviationMap.put("ECO:0000316", "IGI");
        abbreviationMap.put("ECO:0007014", "CEC");
        abbreviationMap.put("ECO:0000315", "IMP");
        abbreviationMap.put("ECO:0007013", "CEA");
        abbreviationMap.put("ECO:0000501", "IEA");
        abbreviationMap.put("ECO:0000021", "IPI");
        abbreviationMap.put("ECO:0000304", "TAS");
        abbreviationMap.put("ECO:0000314", "IDA");
        abbreviationMap.put("ECO:0007191", "IAGP");
        abbreviationMap.put("ECO:0000250", "ISS");
        abbreviationMap.put("ECO:0000033", "TAS");
        abbreviationMap.put("ECO:0000247", "ISA");
        abbreviationMap.put("ECO:0000305", "IC");
    }
    
    public Map<String, EcoTerm> addAbbreviations(Map<String, EcoTerm> termMap) {
        abbreviationMap.forEach((curie, abbreviation) ->
            {
                EcoTerm term = termMap.get(curie);
                if (term == null) {
                    log.info("Could not retrieve " + curie + " to set abbreviation");
                    return;
                }
                term.setAbbreviation(abbreviation);
                List<String> subsets = term.getSubsets();
                if (subsets == null) {
                    subsets = new ArrayList<String>();
                }
                subsets.add(agr_subset);
                term.setSubsets(subsets);
                
            }
        );
        return termMap;
    }
}
