package org.alliancegenome.curation_api.services;

import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
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

	/**
	 * Returns the Species curated for the given taxon curie (e.g. "NCBITaxon:6239"),
	 * or null if none exists. Taxon -> Species is one-to-one, so at most one match.
	 */
	public Species getByTaxonCurie(String taxonCurie) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.TAXON, taxonCurie);
		SearchResponse<Species> resp = speciesDAO.findByParams(params);
		return resp == null ? null : resp.getSingleResult();
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
