package org.alliancegenome.curation_api.services.ontology;

import java.util.List;

import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class DoTermService extends BaseOntologyTermService<DOTerm, DoTermDAO> implements BasePopularityInterface {

	@Inject
	DoTermDAO doTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(doTermDAO);
	}

	@Transactional
	public void updatePopularity(String curie, Double popularity) {
		DOTerm term = findByCurie(curie);
		if (term != null) {
			term.setPopularity(popularity);
		}
	}

	public List<Long> getAllIds() {
		return doTermDAO.getAllIds();
	}

	public List<DOTerm> findByIds(List<Long> ids) {
		return doTermDAO.findByIds(ids);
	}
}
