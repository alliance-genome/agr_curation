package org.alliancegenome.curation_api.services.ontology;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.dao.ontology.NcbiTaxonTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.ObjectResponse;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
@RequestScoped
public class NcbiTaxonTermService extends BaseOntologyTermService<NCBITaxonTerm, NcbiTaxonTermDAO> {

	@Inject NcbiTaxonTermDAO ncbiTaxonTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ncbiTaxonTermDAO);
	}
	
	@Override
	public ObjectResponse<NCBITaxonTerm> get(String taxonCurie) {
		NCBITaxonTerm taxon = ncbiTaxonTermDAO.find(taxonCurie);
		if(taxon == null) {
			taxon = ncbiTaxonTermDAO.downloadAndSave(taxonCurie);
			if(taxon == null) {
				log.warn("Taxon ID could not be found");
			}
		}
		
		return new ObjectResponse<NCBITaxonTerm>(taxon);
	}
	
}
