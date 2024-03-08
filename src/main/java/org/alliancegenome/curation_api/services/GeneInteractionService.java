package org.alliancegenome.curation_api.services;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public abstract class GeneInteractionService<E extends GeneInteraction, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	public ObjectResponse<E> get(String identifier) {
		SearchResponse<E> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
				
		return new ObjectResponse<E>();
	}

}
