package org.alliancegenome.curation_api.controllers.base;

import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseEntityDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.response.ObjectListResponse;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadHelper;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

public abstract class BaseOntologyTermController<S extends BaseOntologyTermService<E, D>, E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseEntityCrudController<S, E, BaseEntityDAO<E>> {

	private GenericOntologyLoadHelper<E> loader;

	private BaseOntologyTermService<E, D> service;
	private Class<E> termClazz;

	protected void setService(S service, Class<E> termClazz) {
		super.setService(service);
		this.service = service;
		this.termClazz = termClazz;
		loader = new GenericOntologyLoadHelper<E>(termClazz);
	}

	protected void setService(S service, Class<E> termClazz, GenericOntologyLoadConfig config) {
		super.setService(service);
		this.service = service;
		this.termClazz = termClazz;
		loader = new GenericOntologyLoadHelper<E>(termClazz, config);
	}

	public String updateTerms(boolean async, String fullText) {
		try {
			Map<String, E> termMap = loader.load(fullText);
			ProcessDisplayHelper ph = new ProcessDisplayHelper(10000);
			ph.startProcess(termClazz.getSimpleName() + " Database Persistance", termMap.size());
			for (String termKey : termMap.keySet()) {
				service.processUpdate(termMap.get(termKey));
				ph.progressProcess();
			}
			ph.finishProcess();
		} catch (Exception e) {
			e.printStackTrace();
			return "FAIL";
		}
		return "OK";
	}

	public ObjectListResponse<E> getRootNodes() {
		return service.getRootNodes();
	}

	public ObjectListResponse<E> getChildren(String curie) {
		return service.getChildren(curie);
	}

	public ObjectListResponse<E> getDescendants(String curie) {
		return service.getDescendants(curie);
	}

	public ObjectListResponse<E> getParents(String curie) {
		return service.getParents(curie);
	}

	public ObjectListResponse<E> getAncestors(String curie) {
		return service.getAncestors(curie);
	}
}
