package org.alliancegenome.curation_api.dao.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTermClosure;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

@ApplicationScoped
public class OntologyTermClosureDAO extends BaseSQLDAO<OntologyTermClosure> {

	protected OntologyTermClosureDAO() {
		super(OntologyTermClosure.class);
	}

	// Returns closure-row IDs for the given ontology term type whose closureTypes equal the requested set
	// and whose ancestor (closureObject) is non-obsolete. Both ends of the row must be of the requested type.
	// closureTypes is JSONB; equality is filtered in-memory after fetching candidate rows for the type.
	public List<Long> getAllIds(String ontologyTermType, Set<String> relationTypes) {
		Class<?> termClass = entityManager.getMetamodel().getEntities().stream()
			.filter(t -> OntologyTerm.class.isAssignableFrom(t.getJavaType()))
			.filter(t -> t.getJavaType().getSimpleName().equals(ontologyTermType))
			.map(t -> t.getJavaType())
			.findFirst()
			.orElseThrow(() -> new BadRequestException("Unknown ontology term type: " + ontologyTermType));

		String jpql = """
				SELECT c.id, c.closureTypes FROM OntologyTermClosure c
				JOIN c.closureSubject sub
				JOIN c.closureObject obj
				WHERE TYPE(sub) = :termType
				AND TYPE(obj) = :termType
				AND (obj.obsolete = false OR obj.obsolete IS NULL)
				""";

		List<Object[]> rows = entityManager
			.createQuery(jpql, Object[].class)
			.setParameter("termType", termClass)
			.getResultList();

		List<Long> ids = new ArrayList<>(rows.size());
		for (Object[] row : rows) {
			@SuppressWarnings("unchecked")
			Set<String> rowTypes = (Set<String>) row[1];
			if (rowTypes != null && rowTypes.equals(relationTypes)) {
				ids.add((Long) row[0]);
			}
		}
		return ids;
	}

	public List<OntologyTermClosure> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String jpql = """
				SELECT c FROM OntologyTermClosure c
				JOIN FETCH c.closureSubject
				JOIN FETCH c.closureObject
				WHERE c.id IN :ids
				""";
		return entityManager
			.createQuery(jpql, OntologyTermClosure.class)
			.setParameter("ids", ids)
			.getResultList();
	}

}
