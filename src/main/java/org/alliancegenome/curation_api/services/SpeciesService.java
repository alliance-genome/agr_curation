package org.alliancegenome.curation_api.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.SpeciesValidator;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class SpeciesService extends BaseEntityCrudService<Species, SpeciesDAO> {

	@Inject SpeciesDAO speciesDAO;
	@Inject SpeciesValidator speciesValidator;

	Date speciesRequest;
	HashMap<String, Species> displayNameCacheMap = new HashMap<>();
	HashMap<String, Species> taxonCurieCacheMap = new HashMap<>();

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

	public Species getByDisplayName(String displayName) {
		if (displayName == null) {
			return null;
		}

		if (speciesRequest != null) {
			if (displayNameCacheMap.containsKey(displayName)) {
				return displayNameCacheMap.get(displayName);
			}
		} else {
			speciesRequest = new Date();
		}

		Log.debug("Species not cached, caching species: (" + displayName + ")");
		SearchResponse<Species> response = speciesDAO.findByField("displayName", displayName);
		Species species = null;
		if (response != null) {
			species = response.getSingleResult();
		}
		displayNameCacheMap.put(displayName, species);
		return species;
	}

	public Species getByTaxonCurie(String taxonCurie) {
		if (taxonCurie == null) {
			return null;
		}

		if (speciesRequest != null) {
			if (taxonCurieCacheMap.containsKey(taxonCurie)) {
				return taxonCurieCacheMap.get(taxonCurie);
			}
		} else {
			speciesRequest = new Date();
		}

		Log.debug("Species not cached by taxon, caching species: (" + taxonCurie + ")");
		SearchResponse<Species> response = speciesDAO.findByField(EntityFieldConstants.TAXON, taxonCurie);
		Species species = null;
		if (response != null) {
			species = response.getSingleResult();
		}
		taxonCurieCacheMap.put(taxonCurie, species);
		return species;
	}
}
