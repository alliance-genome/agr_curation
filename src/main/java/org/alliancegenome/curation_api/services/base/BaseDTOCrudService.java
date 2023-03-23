package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.base.BaseEntity;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public abstract class BaseDTOCrudService<E extends BaseEntity, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	protected abstract void init();

	public abstract E upsert(T dto) throws ObjectUpdateException;

	public abstract void removeOrDeprecateNonUpdated(String curie, String dataProvider);

}
