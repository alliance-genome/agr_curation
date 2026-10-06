package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.TransgenicTool;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

/** SCRUM-6535. Mirrors ConstructDAO, the closest existing Reagent subclass DAO. */
@ApplicationScoped
public class TransgenicToolDAO extends BaseCurieSQLDAO<TransgenicTool> {

	protected TransgenicToolDAO() {
		super(TransgenicTool.class);
	}

	/**
	 * Identifiers to ids, for resolving the subject or object of an association during a load.
	 *
	 * Reads FROM Reagent rather than FROM transgenictool: under the JOINED strategy primaryExternalId and
	 * modInternalId live on the parent, so selecting them from the subclass table would not compile
	 * in SQL.
	 */
	public Map<String, Long> getTransgenicToolIdMap() {
		Map<String, Long> idMap = new HashMap<>();
		Query q = entityManager.createNativeQuery("SELECT a.id, a." + EntityFieldConstants.PRIMARY_EXTERNAL_ID + ", a." + EntityFieldConstants.MOD_INTERNAL_ID + " FROM Reagent as a where exists (select * from transgenictool as g where g.id = a.id)");
		List<Object[]> ids = q.getResultList();
		ids.forEach(record -> {
			if (record[1] != null) {
				idMap.put((String) record[1], (long) record[0]);
			}
			if (record[2] != null) {
				idMap.put((String) record[2], (long) record[0]);
			}
		});
		return idMap;
	}

}
