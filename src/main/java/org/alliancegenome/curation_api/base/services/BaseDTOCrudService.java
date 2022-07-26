package org.alliancegenome.curation_api.base.services;

import org.alliancegenome.curation_api.base.BaseDTO;
import org.alliancegenome.curation_api.base.dao.BaseEntityDAO;
import org.alliancegenome.curation_api.base.entity.BaseEntity;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;


public abstract class BaseDTOCrudService<E extends BaseEntity, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	protected abstract void init();
	public abstract E upsert(T dto) throws ObjectUpdateException;


}
