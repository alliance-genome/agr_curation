package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Construct;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class ConstructDAO extends BaseSQLDAO<Construct> {

	protected ConstructDAO() {
		super(Construct.class);
	}

	public Map<String, Long> getConstructIdMap() {
		Map<String, Long> constructIdMap = new HashMap<>();
		Query q = entityManager.createNativeQuery("SELECT a.id, a." + EntityFieldConstants.PRIMARY_EXTERNAL_ID + ", a." + EntityFieldConstants.MOD_INTERNAL_ID + " FROM Reagent as a where exists (select * from construct as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				constructIdMap.put((String) record[1], (long) record[0]);
			}
			if (record[2] != null) {
				constructIdMap.put((String) record[2], (long) record[0]);
			}
		});
		return constructIdMap;
	}

}
