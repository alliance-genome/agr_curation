package org.alliancegenome.curation_api.services;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.services.BaseEntityCrudService;
import org.alliancegenome.curation_api.dao.SynonymDAO;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.helpers.validators.SynonymValidator;

@RequestScoped
public class SynonymService extends BaseEntityCrudService<Synonym, SynonymDAO> {

	@Inject
	SynonymDAO synonymDAO;
	@Inject
	SynonymValidator synonymValidator;
	
	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(synonymDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Synonym> update(Synonym uiEntity) {
		Synonym dbEntity = synonymValidator.validateSynonym(uiEntity);
		return new ObjectResponse<>(synonymDAO.persist(dbEntity));
	}
}
