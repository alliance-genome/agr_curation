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

}
