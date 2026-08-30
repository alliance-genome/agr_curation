package org.alliancegenome.curation_api.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CrossReferenceDAO extends BaseSQLDAO<CrossReference> {

	protected CrossReferenceDAO() {
		super(CrossReference.class);
	}

	/**
	 * Returns the ids of the genomic entities that already have a cross reference for one of the given
	 * page areas. Used to preload the state of the Alliance-derived phenotype cross references so that
	 * an annotation load only has to write the ones that are missing.
	 */
	@SuppressWarnings("unchecked")
	public Set<Long> findGenomicEntityIdsByPageAreas(List<String> pageAreas) {
		Set<Long> genomicEntityIds = new HashSet<>();
		if (CollectionUtils.isEmpty(pageAreas)) {
			return genomicEntityIds;
		}
		List<Object> rows = entityManager
			.createNativeQuery("SELECT DISTINCT gx.genomicentity_id FROM genomicentity_crossreference gx JOIN crossreference x ON x.id = gx.crossreferences_id JOIN resourcedescriptorpage rdp ON rdp.id = x.resourcedescriptorpage_id WHERE rdp.name IN (:pageAreas)")
			.setParameter("pageAreas", pageAreas)
			.getResultList();
		for (Object row : rows) {
			if (row != null) {
				genomicEntityIds.add(((Number) row).longValue());
			}
		}
		return genomicEntityIds;
	}

}
