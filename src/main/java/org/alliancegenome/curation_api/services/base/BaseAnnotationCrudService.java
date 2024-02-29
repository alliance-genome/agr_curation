package org.alliancegenome.curation_api.services.base;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;

public abstract class BaseAnnotationCrudService<E extends Annotation, D extends BaseEntityDAO<E>> extends BaseEntityCrudService<E, D> {

	protected abstract void init();

	public abstract E deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate);

}
