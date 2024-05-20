package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.interfaces.crud.BaseUpsertServiceInterface;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

public abstract class BaseAnnotationDTOCrudService<E extends Annotation, T extends BaseDTO, D extends BaseSQLDAO<E>> extends BaseAnnotationCrudService<E, D> implements BaseUpsertServiceInterface<E, T> {
	
	protected abstract void init();
	
}
