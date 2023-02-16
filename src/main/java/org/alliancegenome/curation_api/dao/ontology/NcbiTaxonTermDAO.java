package org.alliancegenome.curation_api.dao.ontology;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.interfaces.ncbi.NCBIRESTInterface;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.NCBITaxonResponseDTO;

import si.mazi.rescu.RestProxyFactory;

@ApplicationScoped
public class NcbiTaxonTermDAO extends BaseSQLDAO<NCBITaxonTerm> {
	private final int MAX_ATTEMPTS = 5;

	protected NcbiTaxonTermDAO() {
		super(NCBITaxonTerm.class);
	}

	private NCBIRESTInterface api = RestProxyFactory.createProxy(NCBIRESTInterface.class, "https://eutils.ncbi.nlm.nih.gov");

	public NCBITaxonTerm downloadAndSave(String taxonCurie) {
		
		Pattern taxonIdPattern = Pattern.compile("^NCBITaxon:(\\d+)$");
		Matcher taxonIdMatcher = taxonIdPattern.matcher(taxonCurie);
		if (!taxonIdMatcher.find()) {
			return null;
		}

		HashMap<String, Object> taxonMap = null;
		int attemptsRemaining = MAX_ATTEMPTS;
		while (attemptsRemaining-- > 0) {
			try {
				NCBITaxonResponseDTO resp = api.getTaxonFromNCBI("taxonomy", "json", taxonIdMatcher.group(1));
				HashMap<String, Object> result = resp.getResult();
				taxonMap = (HashMap<String, Object>) result.get(taxonIdMatcher.group(1));

				break;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}

		if (taxonMap == null || taxonMap.get("error") != null)
			return null;

		String name = (String) taxonMap.get("scientificname");
		NCBITaxonTerm taxon = new NCBITaxonTerm();
		taxon.setName(name);
		taxon.setCurie(taxonCurie);
		if (taxonMap.get("status").equals("active")) {
			taxon.setObsolete(false);
		} else {
			taxon.setObsolete(true);
		}
		persist(taxon);

		return taxon;
	}

}
