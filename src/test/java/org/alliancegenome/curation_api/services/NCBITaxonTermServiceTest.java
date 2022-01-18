package org.alliancegenome.curation_api.services;

import java.util.HashMap;

import org.alliancegenome.curation_api.interfaces.ncbi.NCBIRESTInterface;
import org.alliancegenome.curation_api.model.ingest.NCBITaxonResponceDTO;

import si.mazi.rescu.RestProxyFactory;

public class TestNCBI {

    public static void main(String[] args) {
        NCBIRESTInterface api = RestProxyFactory.createProxy(NCBIRESTInterface.class, "https://eutils.ncbi.nlm.nih.gov");
        
        NCBITaxonResponceDTO taxon = api.getTaxonFromNCBI("taxonomy", "json", "9606");
        HashMap<String, Object> result = taxon.getResult();
        HashMap<String, Object> taxonMap = (HashMap<String, Object>)result.get("9606");
        System.out.println(taxonMap);
    }

}
