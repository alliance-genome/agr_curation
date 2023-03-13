package org.alliancegenome.curation_api.controllers.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.model.entities.base.BaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;
import org.alliancegenome.curation_api.services.base.BaseDTOCrudService;

public abstract class BaseDTOCrudController<S extends BaseDTOCrudService<E, T, D>, E extends BaseEntity, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudController<S, E, D>
	implements BaseDTOCrudControllerInterface<E, T> {

	protected abstract void init();

	private BaseDTOCrudService<E, T, D> service;

	protected void setService(S service) {
		super.setService(service);
		this.service = service;
	}

	public E upsert(T dto) throws ObjectUpdateException {
		return service.upsert(dto, null);
	}

}
