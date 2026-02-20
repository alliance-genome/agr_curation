package org.alliancegenome.curation_api.services.ontology;

import java.util.List;

import org.alliancegenome.curation_api.dao.ontology.GoTermDAO;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GoTermService extends BaseOntologyTermService<GOTerm, GoTermDAO> implements BasePopularityInterface {

	@Inject
	GoTermDAO goTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(goTermDAO);
	}
	
	@Transactional
	public void updatePopularity(String curie, Double popularity) {
		GOTerm term = findByCurie(curie);
		if (term != null) {
			term.setPopularity(popularity);
		}
	}

	public List<Long> getAllGOSearchResultIds() {
		return goTermDAO.getAllGOSearchResultIds();
	}

	public List<GOTerm> findByIds(List<Long> ids) {
		return goTermDAO.findByIds(ids);
	}

}
