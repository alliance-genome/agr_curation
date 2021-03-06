package org.alliancegenome.curation_api.base.controllers;

import java.util.Map;

import org.alliancegenome.curation_api.base.dao.BaseEntityDAO;
import org.alliancegenome.curation_api.base.services.BaseOntologyTermService;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.services.helpers.*;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;

public abstract class BaseOntologyTermController<S extends BaseOntologyTermService<E, D>, E extends OntologyTerm, D extends BaseEntityDAO<E>> extends BaseCrudController<S, E, BaseEntityDAO<E>> {

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
			for(String termKey: termMap.keySet()) {
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
}
