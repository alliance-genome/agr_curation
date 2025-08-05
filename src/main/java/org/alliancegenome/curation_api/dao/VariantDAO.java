package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;

	protected VariantDAO() {
		super(Variant.class);
	}

	public List<String> getAllVariantPrimaryExternalIds() {
		String sql = """
			SELECT be.primaryexternalid
			FROM biologicalentity be, variant as v
			WHERE be.id = v.id and be.primaryexternalid is not NULL
		""";
		
		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();
		
		objects.forEach(object -> {
			list.add((String) object);
		});
		
		return list;
	}
}
