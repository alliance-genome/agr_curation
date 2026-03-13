package org.alliancegenome.curation_api.dao.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Query;

@ApplicationScoped
public class DoTermDAO extends BaseSQLDAO<DOTerm> {

	protected DoTermDAO() {
		super(DOTerm.class);
	}

	public List<String> getDoTermCuries() {
		String sql = """
			SELECT curie
			FROM ontologyterm
			WHERE ontologytermtype = 'DOTerm'
		""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();

		objects.forEach(object -> {
			list.add((String) object);
		});

		return list;
	}

	public List<Long> getAllIds() {
		String sql = """
			SELECT id
			FROM ontologyterm
			WHERE ontologytermtype = 'DOTerm'
			AND obsolete = false
			AND internal = false
			ORDER BY id
			""";
		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<DOTerm> findByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String jpql = """
			SELECT DISTINCT d FROM DOTerm d
			LEFT JOIN FETCH d.synonyms
			WHERE d.id IN :ids
			""";
		return entityManager.createQuery(jpql, DOTerm.class)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findBaseFieldsByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT id, curie, name, definition
			FROM ontologyterm
			WHERE id IN (:ids)
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findSynonymsByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT os.ontologyterm_id, s.name
			FROM ontologyterm_synonym os
			JOIN synonym s ON s.id = os.synonyms_id
			WHERE os.ontologyterm_id IN (:ids)
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findCrossReferencesByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT ocr.ontologyterm_id, cr.displayname
			FROM ontologyterm_crossreference ocr
			JOIN crossreference cr ON cr.id = ocr.crossreferences_id
			WHERE ocr.ontologyterm_id IN (:ids)
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findSecondaryIdsByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT ontologyterm_id, secondaryidentifiers
			FROM ontologyterm_secondaryidentifiers
			WHERE ontologyterm_id IN (:ids)
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findGenesAndSpeciesByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			WITH direct_genes AS (
				SELECT da.diseaseannotationobject_id AS doterm_id, gda.diseaseannotationsubject_id AS gene_id
				FROM diseaseannotation da
				JOIN genediseaseannotation gda ON gda.id = da.id
				WHERE da.diseaseannotationobject_id IN (:ids) AND da.internal = false AND da.obsolete = false
				UNION
				SELECT da.diseaseannotationobject_id, agmda.inferredgene_id
				FROM diseaseannotation da
				JOIN agmdiseaseannotation agmda ON agmda.id = da.id
				WHERE da.diseaseannotationobject_id IN (:ids) AND da.internal = false AND da.obsolete = false
					AND agmda.inferredgene_id IS NOT NULL
				UNION
				SELECT da.diseaseannotationobject_id, ag.assertedgenes_id
				FROM diseaseannotation da
				JOIN agmdiseaseannotation agmda ON agmda.id = da.id
				JOIN agmdiseaseannotation_gene ag ON ag.agmdiseaseannotation_id = agmda.id
				WHERE da.diseaseannotationobject_id IN (:ids) AND da.internal = false AND da.obsolete = false
				UNION
				SELECT da.diseaseannotationobject_id, ada.inferredgene_id
				FROM diseaseannotation da
				JOIN allelediseaseannotation ada ON ada.id = da.id
				WHERE da.diseaseannotationobject_id IN (:ids) AND da.internal = false AND da.obsolete = false
					AND ada.inferredgene_id IS NOT NULL
				UNION
				SELECT da.diseaseannotationobject_id, adag.assertedgenes_id
				FROM diseaseannotation da
				JOIN allelediseaseannotation ada ON ada.id = da.id
				JOIN allelediseaseannotation_gene adag ON adag.allelediseaseannotation_id = ada.id
				WHERE da.diseaseannotationobject_id IN (:ids) AND da.internal = false AND da.obsolete = false
			),
			all_genes AS (
				SELECT doterm_id, gene_id FROM direct_genes
				UNION
				SELECT dg.doterm_id, g2g.objectgene_id
				FROM direct_genes dg
				JOIN genetogeneorthology g2g ON g2g.subjectgene_id = dg.gene_id
				JOIN genetogeneorthologygenerated g2gg ON g2gg.id = g2g.id AND g2gg.strictfilter = true
			)
			SELECT ag.doterm_id, sa.displaytext AS gene_symbol, sp.abbreviation,
				split_part(taxon.name, ' ', 1) || ' ' || split_part(taxon.name, ' ', 2) AS genus_species
			FROM all_genes ag
			JOIN slotannotation sa ON sa.singlegene_id = ag.gene_id AND sa.slotannotationtype = 'GeneSymbolSlotAnnotation'
			JOIN biologicalentity be ON be.id = ag.gene_id
			JOIN ontologyterm taxon ON taxon.id = be.taxon_id
			JOIN species sp ON sp.taxon_id = taxon.id
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

	public List<Object[]> findDiseaseGroupByIds(List<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT otc.closuresubject_id, ancestor.name
			FROM ontologytermclosure otc
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			JOIN ontologyterm_subsets sub ON sub.ontologyterm_id = ancestor.id
			WHERE otc.closuresubject_id IN (:ids)
				AND sub.subsets = 'DO_AGR_slim'
			""";
		return entityManager.createNativeQuery(sql)
			.setParameter("ids", ids)
			.getResultList();
	}

}
