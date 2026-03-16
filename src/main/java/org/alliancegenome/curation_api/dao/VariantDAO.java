package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.response.ObjectListResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {

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

	public ObjectListResponse<String> getAllVariantNames() {

		String sql = """
				SELECT DISTINCT hgvs from curatedvariantgenomiclocation
			""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> hgvsList = new ArrayList<>();
		objects.forEach(object -> {
			hgvsList.add((String) object);
		});
		ObjectListResponse<String> ret = new ObjectListResponse<>();
		ret.setEntities(hgvsList);
		return ret;
	}
}
