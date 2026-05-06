package org.alliancegenome.curation_api.services.ontology;

import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.dao.ontology.OntologyTermClosureDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class OntologyTermClosureService extends BaseEntityCrudService<OntologyTermClosure, OntologyTermClosureDAO> {

	@Inject
	OntologyTermClosureDAO ontologyTermClosureDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(ontologyTermClosureDAO);
	}

	public List<Long> getAllIds(String ontologyTermType, Set<String> relationTypes) {
		return ontologyTermClosureDAO.getAllIds(ontologyTermType, relationTypes);
	}

	public List<OntologyTermClosure> findByIds(List<Long> ids) {
		return ontologyTermClosureDAO.findByIds(ids);
	}

}
