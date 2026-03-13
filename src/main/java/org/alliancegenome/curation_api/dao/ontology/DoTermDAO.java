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

	public List<Object[]> findAllBaseFields() {
		String sql = """
			SELECT id, curie, name, definition
			FROM ontologyterm
			WHERE ontologytermtype = 'DOTerm' AND obsolete = false AND internal = false
			ORDER BY id
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findAllSynonyms() {
		String sql = """
			SELECT os.ontologyterm_id, s.name
			FROM ontologyterm_synonym os
			JOIN synonym s ON s.id = os.synonyms_id
			JOIN ontologyterm ot ON ot.id = os.ontologyterm_id
			WHERE ot.ontologytermtype = 'DOTerm' AND ot.obsolete = false AND ot.internal = false
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findAllCrossReferences() {
		String sql = """
			SELECT ocr.ontologyterm_id, cr.displayname
			FROM ontologyterm_crossreference ocr
			JOIN crossreference cr ON cr.id = ocr.crossreferences_id
			JOIN ontologyterm ot ON ot.id = ocr.ontologyterm_id
			WHERE ot.ontologytermtype = 'DOTerm' AND ot.obsolete = false AND ot.internal = false
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findAllSecondaryIds() {
		String sql = """
			SELECT osi.ontologyterm_id, osi.secondaryidentifiers
			FROM ontologyterm_secondaryidentifiers osi
			JOIN ontologyterm ot ON ot.id = osi.ontologyterm_id
			WHERE ot.ontologytermtype = 'DOTerm' AND ot.obsolete = false AND ot.internal = false
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findAllGeneSymbols() {
		String sql = """
			SELECT sa.singlegene_id, sa.displaytext, sp.abbreviation,
				split_part(taxon.name, ' ', 1) || ' ' || split_part(taxon.name, ' ', 2) AS genus_species
			FROM slotannotation sa
			JOIN biologicalentity be ON be.id = sa.singlegene_id
			JOIN ontologyterm taxon ON taxon.id = be.taxon_id
			JOIN species sp ON sp.taxon_id = taxon.id
			WHERE sa.slotannotationtype = 'GeneSymbolSlotAnnotation'
			AND sa.singlegene_id IS NOT NULL
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findDiseaseGeneIds() {
		String sql = """
			WITH direct_genes AS (
				SELECT da.diseaseannotationobject_id AS doterm_id, gda.diseaseannotationsubject_id AS gene_id
				FROM diseaseannotation da
				JOIN genediseaseannotation gda ON gda.id = da.id
				WHERE da.internal = false AND da.obsolete = false
				UNION
				SELECT da.diseaseannotationobject_id, agmda.inferredgene_id
				FROM diseaseannotation da
				JOIN agmdiseaseannotation agmda ON agmda.id = da.id
				WHERE da.internal = false AND da.obsolete = false AND agmda.inferredgene_id IS NOT NULL
				UNION
				SELECT da.diseaseannotationobject_id, ag.assertedgenes_id
				FROM diseaseannotation da
				JOIN agmdiseaseannotation agmda ON agmda.id = da.id
				JOIN agmdiseaseannotation_gene ag ON ag.agmdiseaseannotation_id = agmda.id
				WHERE da.internal = false AND da.obsolete = false
				UNION
				SELECT da.diseaseannotationobject_id, ada.inferredgene_id
				FROM diseaseannotation da
				JOIN allelediseaseannotation ada ON ada.id = da.id
				WHERE da.internal = false AND da.obsolete = false AND ada.inferredgene_id IS NOT NULL
				UNION
				SELECT da.diseaseannotationobject_id, adag.assertedgenes_id
				FROM diseaseannotation da
				JOIN allelediseaseannotation ada ON ada.id = da.id
				JOIN allelediseaseannotation_gene adag ON adag.allelediseaseannotation_id = ada.id
				WHERE da.internal = false AND da.obsolete = false
			)
			SELECT doterm_id, gene_id FROM direct_genes
			UNION
			SELECT dg.doterm_id, g2g.objectgene_id
			FROM direct_genes dg
			JOIN genetogeneorthology g2g ON g2g.subjectgene_id = dg.gene_id
			JOIN genetogeneorthologygenerated g2gg ON g2gg.id = g2g.id AND g2gg.strictfilter = true
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public List<Object[]> findAllDiseaseGroups() {
		String sql = """
			SELECT DISTINCT otc.closuresubject_id, ancestor.name
			FROM ontologytermclosure otc
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			JOIN ontologyterm_subsets sub ON sub.ontologyterm_id = ancestor.id
			JOIN ontologyterm doterm ON doterm.id = otc.closuresubject_id
			WHERE sub.subsets = 'DO_AGR_slim'
				AND doterm.ontologytermtype = 'DOTerm' AND doterm.obsolete = false AND doterm.internal = false
			""";
		return entityManager.createNativeQuery(sql).getResultList();
	}

}
