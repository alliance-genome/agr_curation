package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class TransgenicToolDAO extends BaseSQLDAO<TransgenicTool> {

	protected TransgenicToolDAO() {
		super(TransgenicTool.class);
	}

	public Map<String, Long> getTransgenicToolIdMap() {
		Map<String, Long> transgenicToolIdMap = new HashMap<>();
		Query q = entityManager.createNativeQuery("SELECT a.id, a." + EntityFieldConstants.PRIMARY_EXTERNAL_ID + ", a." + EntityFieldConstants.MOD_INTERNAL_ID + " FROM Reagent as a where exists (select * from transgenictool as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				transgenicToolIdMap.put((String) record[1], (long) record[0]);
			}
			if (record[2] != null) {
				transgenicToolIdMap.put((String) record[2], (long) record[0]);
			}
		});
		return transgenicToolIdMap;
	}

}
