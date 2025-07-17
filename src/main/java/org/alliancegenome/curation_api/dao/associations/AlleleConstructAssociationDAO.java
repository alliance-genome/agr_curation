package org.alliancegenome.curation_api.dao.associations;

import static org.alliancegenome.curation_api.constants.EntityFieldConstants.ALLELE_ASSOCIATION_SUBJECT;
import static org.alliancegenome.curation_api.constants.EntityFieldConstants.ALLELE_CONSTRUCT_ASSOCIATION_OBJECT;
import static org.alliancegenome.curation_api.constants.EntityFieldConstants.CONSTRUCT_ASSOCIATION_SUBJECT;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.jboss.logging.Logger;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class AlleleConstructAssociationDAO extends BaseSQLDAO<AlleleConstructAssociation> {

	protected AlleleConstructAssociationDAO() {
		super(AlleleConstructAssociation.class);
	}

	public SearchResponse<AlleleConstructAssociation> findAllAssociations(Pagination pagination, HashMap<String, Object> params) {
		Logger.Level level = Logger.Level.DEBUG;
		if (params.containsKey("debug")) {
			level = params.remove("debug").equals("true") ? Logger.Level.INFO : Logger.Level.DEBUG;
		}

		Log.log(level, "Pagination: " + pagination + " Params: " + params + " Class: " + myClass);

		TypedQuery<AlleleConstructAssociation> query = entityManager.createQuery("""
								select aca
				from AlleleConstructAssociation aca, ConstructGenomicEntityAssociation cgea
				where aca.alleleConstructAssociationObject = cgea.constructAssociationSubject
				and ((:params is not null AND (aca.alleleAssociationSubject.primaryExternalId = :params)) OR (:params is null))
				order by aca.id
				""", AlleleConstructAssociation.class);
		if (params.get("allele.primaryExternalId") != null) {
			query.setParameter("params", params.get("allele.primaryExternalId"));
		} else {
			query.setParameter("params", null);
		}
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0) {
				first = 0;
			}
			query.setFirstResult(first);
			query.setMaxResults(pagination.getLimit());
		}
		String queryString = """
				select count(*)
				from AlleleConstructAssociation aca, ConstructGenomicEntityAssociation cgea
				where aca.alleleConstructAssociationObject = cgea.constructAssociationSubject
				and ((:params is not null AND (aca.alleleAssociationSubject.primaryExternalId = :params)) OR (:params is null))
				""";
		queryString = queryString.replaceAll("alleleConstructAssociationObject", ALLELE_CONSTRUCT_ASSOCIATION_OBJECT);
		queryString = queryString.replaceAll("constructAssociationSubject", CONSTRUCT_ASSOCIATION_SUBJECT);
		queryString = queryString.replaceAll("alleleAssociationSubject", ALLELE_ASSOCIATION_SUBJECT);
		TypedQuery<Long> countQuery = entityManager.createQuery(queryString, Long.class);
		if (params.get("allele.primaryExternalId") != null) {
			countQuery.setParameter("params", params.get("allele.primaryExternalId"));
		} else {
			countQuery.setParameter("params", null);
		}

		SearchResponse<AlleleConstructAssociation> results = new SearchResponse<>();

		if (level == Logger.Level.INFO) {
			results.setDebug("true");
			results.setEsQuery(((QuerySqmImpl<?>) query).getQueryString());
			results.setDbQuery(((SqmSelectStatement) query).toHqlString());
		}

		if (pagination != null && pagination.getPage() == 0 && pagination.getLimit() == 0) { // If pagination is null then there is no point in getting the total results
			Long totalResults = countQuery.getSingleResult();
			results.setTotalResults(totalResults);
		} else {
			List<AlleleConstructAssociation> dbResults = query.getResultList();
			results.setResults(dbResults);
		}
		return results;
	}
}
