package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.*;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;

public class DoTermService extends BaseService<DOTerm, DoTermDAO> {

	@Inject DoTermDAO doTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(doTermDAO);
	}

	@Transactional
	public DOTerm upsert(DOTerm dto) {

		DOTerm term = get(dto.getCurie());

		if(term == null) {
			term = new DOTerm();
			term.setCurie(dto.getCurie());
		}
		term.setName(dto.getName());
		term.setDefinition(dto.getDefinition());
		doTermDAO.persist(term);

		return term;

	}

}
