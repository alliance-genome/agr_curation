package org.alliancegenome.curation_api.base;

import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;

public abstract class BaseOntologyTermController<S extends BaseOntologyTermService<E, D>, E extends OntologyTerm, D extends BaseDAO<E>> extends BaseController<S, E, BaseDAO<E>> {

}
