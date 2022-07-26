package org.alliancegenome.curation_api.base.controllers;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.base.dao.BaseEntityDAO;
import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.base.interfaces.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.base.services.BaseDTOCrudService;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;

public abstract class BaseDTOCrudController<S extends BaseDTOCrudService<E, T, D>, E extends BaseEntity, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudController<S, E, D> implements BaseDTOCrudControllerInterface<E, T> {

	protected abstract void init();
	
	private BaseDTOCrudService<E, T, D> service;

	protected void setService(S service) {
		super.setService(service);
		this.service = service;
	}
	
	public E upsert(T dto) throws ObjectUpdateException {
		return service.upsert(dto);
	}

}
