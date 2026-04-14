package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.SpeciesValidator;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SpeciesService extends BaseEntityCrudService<Species, SpeciesDAO> {

	@Inject SpeciesDAO speciesDAO;
	@Inject SpeciesValidator speciesValidator;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(speciesDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Species> update(Species uiEntity) {
		Species dbEntity = speciesValidator.validateSpeciesUpdate(uiEntity);
		return new ObjectResponse<>(speciesDAO.persist(dbEntity));
	}

	@Override
	@Transactional
	public ObjectResponse<Species> create(Species uiEntity) {
		Species dbEntity = speciesValidator.validateSpeciesCreate(uiEntity);
		return new ObjectResponse<>(speciesDAO.persist(dbEntity));
	}
}
