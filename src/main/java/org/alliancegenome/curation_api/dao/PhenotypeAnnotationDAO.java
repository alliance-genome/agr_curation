package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseCurieSQLDAO;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PhenotypeAnnotationDAO extends BaseCurieSQLDAO<PhenotypeAnnotation> {

	protected PhenotypeAnnotationDAO() {
		super(PhenotypeAnnotation.class);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> findUniqueIdsByDataProvider(String sourceOrganization) {
		List<Object[]> rows = entityManager
			.createNativeQuery("SELECT pa.id, pa.uniqueid FROM phenotypeannotation pa JOIN organization o ON pa.dataprovider_id = o.id WHERE o.abbreviation = :sourceOrg AND pa.uniqueid IS NOT NULL")
			.setParameter("sourceOrg", sourceOrganization)
			.getResultList();
		Map<String, Long> uniqueIdMap = new HashMap<>(rows.size());
		for (Object[] row : rows) {
			Long id = ((Number) row[0]).longValue();
			String uniqueId = (String) row[1];
			uniqueIdMap.put(uniqueId, id);
		}
		return uniqueIdMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> findInferredGeneIdsByDataProvider(String sourceOrganization) {
		Map<String, Long> map = new HashMap<>();
		// Allele phenotype annotations
		List<Object[]> alleleRows = entityManager
			.createNativeQuery("SELECT pa.uniqueid, apa.inferredgene_id FROM phenotypeannotation pa JOIN allelephenotypeannotation apa ON pa.id = apa.id JOIN organization o ON pa.dataprovider_id = o.id WHERE o.abbreviation = :sourceOrg AND apa.inferredgene_id IS NOT NULL AND pa.uniqueid IS NOT NULL")
			.setParameter("sourceOrg", sourceOrganization)
			.getResultList();
		for (Object[] row : alleleRows) {
			map.put((String) row[0], ((Number) row[1]).longValue());
		}
		// AGM phenotype annotations
		List<Object[]> agmRows = entityManager
			.createNativeQuery("SELECT pa.uniqueid, apa.inferredgene_id FROM phenotypeannotation pa JOIN agmphenotypeannotation apa ON pa.id = apa.id JOIN organization o ON pa.dataprovider_id = o.id WHERE o.abbreviation = :sourceOrg AND apa.inferredgene_id IS NOT NULL AND pa.uniqueid IS NOT NULL")
			.setParameter("sourceOrg", sourceOrganization)
			.getResultList();
		for (Object[] row : agmRows) {
			map.put((String) row[0], ((Number) row[1]).longValue());
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Long> findInferredAlleleIdsByDataProvider(String sourceOrganization) {
		List<Object[]> rows = entityManager
			.createNativeQuery("SELECT pa.uniqueid, apa.inferredallele_id FROM phenotypeannotation pa JOIN agmphenotypeannotation apa ON pa.id = apa.id JOIN organization o ON pa.dataprovider_id = o.id WHERE o.abbreviation = :sourceOrg AND apa.inferredallele_id IS NOT NULL AND pa.uniqueid IS NOT NULL")
			.setParameter("sourceOrg", sourceOrganization)
			.getResultList();
		Map<String, Long> map = new HashMap<>(rows.size());
		for (Object[] row : rows) {
			map.put((String) row[0], ((Number) row[1]).longValue());
		}
		return map;
	}
}
