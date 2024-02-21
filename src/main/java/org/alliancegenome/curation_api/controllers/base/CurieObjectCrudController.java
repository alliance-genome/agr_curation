package org.alliancegenome.curation_api.controllers.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.interfaces.base.CurieObjectCrudInterface;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.CurieObjectCrudService;

public abstract class CurieObjectCrudController<S extends CurieObjectCrudService<E, D>, E extends CurieObject, D extends BaseEntityDAO<E>> extends BaseEntityCrudController<S, E, D> implements CurieObjectCrudInterface<E> {

	protected CurieObjectCrudService<E, D> service;
		
	protected void setService(S service) {
		super.setService(service);
		this.service = service;
	}
	
	public ObjectResponse<E> get(String curie) {
		return service.get(curie);
	}

	public ObjectResponse<E> delete(String curie) {
		return service.delete(curie);
	}

}
