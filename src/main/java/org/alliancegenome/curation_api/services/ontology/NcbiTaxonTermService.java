package org.alliancegenome.curation_api.services.ontology;


import java.util.Date;
import java.util.HashMap;

import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class NcbiTaxonTermService extends BaseOntologyTermService<NCBITaxonTerm, NcbiTaxonTermDAO> {

	@Inject
	NcbiTaxonTermDAO ncbiTaxonTermDAO;

	Date taxonRequest = null;
	HashMap<String, NCBITaxonTerm> taxonCacheMap = new HashMap<>();
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ncbiTaxonTermDAO);
	}

	@Override
	public ObjectResponse<NCBITaxonTerm> get(String taxonCurie) {
		NCBITaxonTerm term = null;
		
		if(taxonRequest != null) {
			if(taxonCacheMap.containsKey(taxonCurie)) {
				term = taxonCacheMap.get(taxonCurie);
			} else {
				Log.debug("Term not cached, caching term: (" + taxonCurie + ")");
				term = getTaxonFromDB(taxonCurie);
				taxonCacheMap.put(taxonCurie, term);
			}
		} else {
			term = getTaxonFromDB(taxonCurie);
			taxonRequest = new Date();
		}

		ObjectResponse<NCBITaxonTerm> response = new ObjectResponse<>();
		response.setEntity(term);
		return response;
	}
	
	
	private NCBITaxonTerm getTaxonFromDB(String taxonCurie) {
		NCBITaxonTerm taxon = ncbiTaxonTermDAO.find(taxonCurie);
		if (taxon == null) {
			taxon = ncbiTaxonTermDAO.downloadAndSave(taxonCurie);
			if (taxon == null) {
				Log.warn("Taxon ID could not be found");
			}
		}
		return taxon;
	}

}
