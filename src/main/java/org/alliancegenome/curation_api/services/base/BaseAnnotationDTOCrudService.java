package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public abstract class BaseAnnotationDTOCrudService<E extends Annotation, T extends BaseDTO, D extends BaseSQLDAO<E>> extends BaseAnnotationCrudService<E, D> {

	protected abstract void init();

	public E upsert(T dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}
	
	public abstract E upsert(T dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException;

}
