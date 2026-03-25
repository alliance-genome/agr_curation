package org.alliancegenome.curation_api.services.ontology;

import java.util.Map;

import org.alliancegenome.curation_api.constants.VariantConstants;
import org.alliancegenome.curation_api.dao.ontology.SoTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SoTermService extends BaseOntologyTermService<SOTerm, SoTermDAO> {

	@Inject
	SoTermDAO soTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(soTermDAO);
	}

	@Transactional
	public void updateSeverityRankings() {
		for (Map.Entry<String, Integer> entry : VariantConstants.SORTED_VARIANT_CONSEQUENCE_MAP.entrySet()) {
			SearchResponse<SOTerm> response = soTermDAO.findByField("name", entry.getKey());
			if (response != null && response.getSingleResult() != null) {
				SOTerm soTerm = response.getSingleResult();
				soTerm.setSeverityOrder(entry.getValue());
				soTermDAO.persist(soTerm);
			} else {
				Log.warn("SO term not found for severity ranking: " + entry.getKey());
			}
		}
	}

}
