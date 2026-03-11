package org.alliancegenome.curation_api.dao;

import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class PredictedVariantConsequenceDAO extends BaseSQLDAO<PredictedVariantConsequence> {

	protected PredictedVariantConsequenceDAO() {
		super(PredictedVariantConsequence.class);
	}

	@SuppressWarnings("unchecked")
	public void populateIntronExonLocations(Map<Long, PredictedVariantConsequence> pvcById) {
		if (pvcById == null || pvcById.isEmpty()) {
			return;
		}

		String intronExonQueryString = """
			WITH exon_data AS (
				SELECT pvc.id as pvc_id,
					cvg.start as vstart,
					egla.start as estart, egla.end as eend,
					egla.strand,
					COUNT(*) OVER (PARTITION BY pvc.id) as total_exons,
					CASE WHEN egla.strand = '-'
						THEN ROW_NUMBER() OVER (PARTITION BY pvc.id ORDER BY egla.start DESC)
						ELSE ROW_NUMBER() OVER (PARTITION BY pvc.id ORDER BY egla.start ASC)
					END as exon_num,
					CASE WHEN cvg.start BETWEEN egla.start AND egla.end THEN true ELSE false END as in_exon
				FROM predictedvariantconsequence pvc
				JOIN curatedvariantgenomiclocation cvg ON cvg.id = pvc.variantgenomiclocation_id
				JOIN transcriptexonassociation tea ON tea.transcriptassociationsubject_id = pvc.varianttranscript_id
				JOIN exongenomiclocationassociation egla ON egla.exonassociationsubject_id = tea.transcriptexonassociationobject_id
				WHERE pvc.id IN :pvcIds
			)
			SELECT pvc_id,
				CASE
					WHEN bool_or(in_exon) THEN
						MAX(CASE WHEN in_exon THEN exon_num END) || '/' || MAX(total_exons)
					ELSE NULL
				END as exon_location,
				CASE
					WHEN NOT bool_or(in_exon) THEN
						(CASE WHEN MAX(strand) = '-'
							THEN MAX(total_exons) - MIN(CASE WHEN estart > vstart THEN exon_num END)
							ELSE MIN(CASE WHEN estart > vstart THEN exon_num END) - 1
						END) || '/' || (MAX(total_exons) - 1)
					ELSE NULL
				END as intron_location
			FROM exon_data
			GROUP BY pvc_id
			""";

		Query query = entityManager.createNativeQuery(intronExonQueryString);
		query.setParameter("pvcIds", pvcById.keySet());
		List<Object[]> results = query.getResultList();

		for (Object[] row : results) {
			Long pvcId = (Long) row[0];
			PredictedVariantConsequence pvc = pvcById.get(pvcId);
			if (pvc != null) {
				pvc.setExons((String) row[1]);
				pvc.setIntrons((String) row[2]);
			}
		}
	}

}
