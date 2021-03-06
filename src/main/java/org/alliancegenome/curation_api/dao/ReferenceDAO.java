package org.alliancegenome.curation_api.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;

@ApplicationScoped
public class ReferenceDAO extends BaseSQLDAO<Reference> {

	protected ReferenceDAO() {
		super(Reference.class);
	}

	public void updateReferenceForeignKeys(String originalCurie, String newCurie) {
		updateReferenceForeignKey("diseaseannotation", "singlereference_curie", originalCurie, newCurie);
		updateReferenceForeignKey("conditionrelation", "singlereference_curie", originalCurie, newCurie);
		updateReferenceForeignKey("note_reference", "references_curie", originalCurie, newCurie);
		updateReferenceForeignKey("paperhandle", "reference_curie", originalCurie, newCurie);
		deleteReferenceForeignKey("reference_crossreference", "reference_curie", originalCurie);
	}

	@Transactional
	protected void updateReferenceForeignKey(String table, String column, String originalCurie, String newCurie) {
		Query jpqlQuery = entityManager.createNativeQuery("UPDATE " + table + " SET " + column + " = '" + newCurie + "' WHERE " + column + " = '" + originalCurie + "'");
		jpqlQuery.executeUpdate();
	}
	
	@Transactional
	protected void deleteReferenceForeignKey(String table, String column, String originalCurie) {
		Query jpqlQuery = entityManager.createNativeQuery("DELETE FROM " + table + " WHERE " + column + " = '" + originalCurie + "'");
		jpqlQuery.executeUpdate();
	}

}
