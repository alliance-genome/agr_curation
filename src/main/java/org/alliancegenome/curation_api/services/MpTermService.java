package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.BaseService;
import org.alliancegenome.curation_api.dao.MpTermDAO;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;

@RequestScoped
public class MpTermService extends BaseService<MPTerm, MpTermDAO> {

	@Inject MpTermDAO mpTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(mpTermDAO);
	}

	@Transactional
	public MPTerm upsert(MPTerm dto) {

		MPTerm term = get(dto.getCurie());

		if(term == null) {
			term = new MPTerm();
			term.setCurie(dto.getCurie());
			term.setName(dto.getName());
			term.setDefinition(dto.getDefinition());
			mpTermDAO.persist(term);
		} else {
			term.setName(dto.getName());
			term.setDefinition(dto.getDefinition());
			mpTermDAO.merge(term);
		}

		return term;

	}

}
