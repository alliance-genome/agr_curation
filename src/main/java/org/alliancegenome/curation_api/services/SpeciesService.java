package org.alliancegenome.curation_api.services;

import java.util.HashMap;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
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

	HashMap<String, Species> displayNameCacheMap = new HashMap<>();
	HashMap<String, Species> taxonCurieCacheMap = new HashMap<>();
	HashMap<String, Boolean> multipleSpeciesCacheMap = new HashMap<>();

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
		if (!displayNameCacheMap.containsKey(displayName)) {
			Log.debug("Species not cached, caching species: (" + displayName + ")");
			SearchResponse<Species> response = displayName == null ? null : speciesDAO.findByField("displayName", displayName);
			displayNameCacheMap.put(displayName, response == null ? null : response.getSingleResult());
		}

		Species species = displayNameCacheMap.get(displayName);
		if (species == null) {
			ObjectResponse<Species> response = new ObjectResponse<>();
			response.addErrorMessage("dataProvider", ValidationConstants.INVALID_MESSAGE + " (" + displayName + ")");
			throw new ApiErrorException(response);
		}
		return species;
	}

	public Species getByTaxonCurie(String taxonCurie) {
		if (taxonCurie == null) {
			return null;
		}

		if (taxonCurieCacheMap.containsKey(taxonCurie)) {
			return taxonCurieCacheMap.get(taxonCurie);
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

	public boolean hasMultipleSpecies(Species species) {
		return multipleSpeciesCacheMap.computeIfAbsent(species.getDataProvider().getAbbreviation(), abbreviation -> {
			SearchResponse<Species> response = speciesDAO.findByField(EntityFieldConstants.DATA_PROVIDER, abbreviation);
			return response != null && response.getResults().size() > 1;
		});
	}

	public String getTaxonFilter(Species species) {
		return hasMultipleSpecies(species) ? species.getTaxon().getCurie() : null;
	}
}
