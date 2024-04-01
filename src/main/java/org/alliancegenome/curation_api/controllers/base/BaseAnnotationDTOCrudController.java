package org.alliancegenome.curation_api.controllers.base;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.interfaces.base.BaseDTOCrudControllerInterface;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.ingest.dto.AnnotationDTO;
import org.alliancegenome.curation_api.services.base.BaseAnnotationDTOCrudService;

public abstract class BaseAnnotationDTOCrudController<S extends BaseAnnotationDTOCrudService<E, T, D>, E extends Annotation, T extends AnnotationDTO, D extends BaseSQLDAO<E>> extends BaseEntityCrudController<S, E, D>
	implements BaseDTOCrudControllerInterface<E, T> {

	protected abstract void init();

	private BaseAnnotationDTOCrudService<E, T, D> service;

	protected void setService(S service) {
		super.setService(service);
		this.service = service;
	}
	
	public E upsert(T dto) throws ObjectUpdateException {
		return upsert(dto, null);
	}

	public E upsert(T dto, BackendBulkDataProvider dataProvider) throws ObjectUpdateException {
		return service.upsert(dto, dataProvider);
	}

}
