package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public abstract class BaseAssociationDTOCrudService<E extends AuditedObject, T extends BaseDTO, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> implements BaseUpsertServiceInterface<E, T>{

	protected abstract void init();

}
