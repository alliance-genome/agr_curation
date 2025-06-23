package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.jboss.logging.Logger;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject
	AgmAlleleAssociationDAO agmAlleleAssociationDAO;

	protected AlleleDAO() {
		super(Allele.class);
	}

	public Boolean hasReferencingDiseaseAnnotationIds(Long alleleId) {

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put(EntityFieldConstants.DA_SUBJECT + ".id", alleleId);
		alleleDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		List<Long> results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);

		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long alleleId) {

		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put(EntityFieldConstants.PA_SUBJECT + ".id", alleleId);
		List<Long> results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmPaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		results = agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams);
		return CollectionUtils.isNotEmpty(results);
	}
	
	public Boolean hasReferencingAgmAlleleAssociations(Long alleleId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ALLELE_ASSOCIATION_OBJECT + ".id", alleleId);
		List<Long> results = agmAlleleAssociationDAO.findIdsByParams(params);
		return CollectionUtils.isNotEmpty(results);
	}

	public SearchResponse<Allele> findAllAllelesWithConstructs(Pagination pagination, HashMap<String, Object> params) {
		Logger.Level level = Logger.Level.DEBUG;
		if (params.containsKey("debug")) {
			level = params.remove("debug").equals("true") ? Logger.Level.INFO : Logger.Level.DEBUG;
		}

		Log.log(level, "Pagination: " + pagination + " Params: " + params + " Class: " + myClass);

		TypedQuery<Allele> query = entityManager.createQuery("""
				from Allele
				where alleleConstructAssociations is not empty
				order by id
				""", Allele.class);
		if (pagination != null && pagination.getLimit() != null && pagination.getPage() != null) {
			int first = pagination.getPage() * pagination.getLimit();
			if (first < 0) {
				first = 0;
			}
			query.setFirstResult(first);
			query.setMaxResults(pagination.getLimit());
		}
		TypedQuery<Long> countQuery = entityManager.createQuery("""
				select count(*)
				from Allele
				where alleleConstructAssociations is not empty
				""", Long.class);

		SearchResponse<Allele> results = new SearchResponse<>();

		if (level == Logger.Level.INFO) {
			results.setDebug("true");
			results.setEsQuery(((QuerySqmImpl<?>) query).getQueryString());
			results.setDbQuery(((SqmSelectStatement) query).toHqlString());
		}

		if (pagination != null && pagination.getPage() == 0 && pagination.getLimit() == 0) { // If pagination is null then there is no point in getting the total results
			Long totalResults = countQuery.getSingleResult();
			results.setTotalResults(totalResults);
		} else {
			List<Allele> dbResults = query.getResultList();
			results.setResults(dbResults);
		}
		return results;
	}
}
