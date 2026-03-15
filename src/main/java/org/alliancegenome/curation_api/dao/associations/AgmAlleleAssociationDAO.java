package org.alliancegenome.curation_api.dao.associations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AgmAlleleAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgmAlleleAssociationDAO extends BaseSQLDAO<AgmAlleleAssociation> {

	protected AgmAlleleAssociationDAO() {
		super(AgmAlleleAssociation.class);
	}

	public AgmAlleleAssociation findBySubjectAndRelationAndObject(Long subjectId, Long relationId, Long objectId) {
		return entityManager
			.createQuery("SELECT a FROM AgmAlleleAssociation a WHERE a.agmAssociationSubject.id = :subjectId AND a.relation.id = :relationId AND a.agmAlleleAssociationObject.id = :objectId", AgmAlleleAssociation.class)
			.setParameter("subjectId", subjectId)
			.setParameter("relationId", relationId)
			.setParameter("objectId", objectId)
			.getResultStream()
			.findFirst()
			.orElse(null);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long>[] findAssociationKeysByDataProvider(String sourceOrganization) {
		List<Object[]> rows = entityManager
			.createNativeQuery("SELECT a.id, a.agmassociationsubject_id, a.relation_id, a.agmalleleassociationobject_id, a.zygosity_id, a.internal, a.obsolete FROM agmalleleassociation a JOIN biologicalentity be ON a.agmassociationsubject_id = be.id JOIN organization o ON be.dataprovider_id = o.id WHERE o.abbreviation = :sourceOrg")
			.setParameter("sourceOrg", sourceOrganization)
			.getResultList();
		Map<String, Long> identityMap = new HashMap<>(rows.size());
		Map<String, Long> fullStateMap = new HashMap<>(rows.size());
		for (Object[] row : rows) {
			Long id = ((Number) row[0]).longValue();
			Long subjectId = ((Number) row[1]).longValue();
			Long relationId = ((Number) row[2]).longValue();
			Long objectId = ((Number) row[3]).longValue();
			Number zygosityId = (Number) row[4];
			Boolean internal = (Boolean) row[5];
			Boolean obsolete = (Boolean) row[6];
			String identityKey = subjectId + "|" + relationId + "|" + objectId;
			identityMap.put(identityKey, id);
			String fullKey = identityKey + "|" + (zygosityId != null ? zygosityId.longValue() : "null") + "|" + internal + "|" + obsolete;
			fullStateMap.put(fullKey, id);
		}
		return new Map[]{identityMap, fullStateMap};
	}

}
