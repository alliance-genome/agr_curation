package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Species;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	@Inject AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject GeneToGeneOrthologyDAO geneToGeneOrthologyDAO;
	@Inject GeneToGeneParalogyDAO geneToGeneParalogyDAO;
	@Inject GeneGeneticInteractionDAO geneGeneticInteractionDAO;
	@Inject GeneMolecularInteractionDAO geneMolecularInteractionDAO;
	@Inject AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject GenePhenotypeAnnotationDAO genePhenotypeAnnotationDAO;
	@Inject GeneExpressionAnnotationDAO geneExpressionAnnotationDAO;

	protected GeneDAO() {
		super(Gene.class);
	}

	public Boolean hasReferencingDiseaseAnnotations(Long geneId) {
		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put("query_operator", "or");
		geneDaParams.put(EntityFieldConstants.DA_SUBJECT + ".id", geneId);
		geneDaParams.put(EntityFieldConstants.DA_MODIFIER_GENES + ".id", geneId);
		geneDaParams.put(EntityFieldConstants.WITH_GENE + ".id", geneId);
		List<Long> results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put(EntityFieldConstants.ASSERTED_GENES + ".id", geneId);
		alleleDaParams.put(EntityFieldConstants.INFERRED_GENE + ".id", geneId);
		alleleDaParams.put(EntityFieldConstants.DA_MODIFIER_GENES + ".id", geneId);
		alleleDaParams.put(EntityFieldConstants.WITH_GENE + ".id", geneId);
		results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put(EntityFieldConstants.ASSERTED_GENES + ".id", geneId);
		agmDaParams.put(EntityFieldConstants.INFERRED_GENE + ".id", geneId);
		agmDaParams.put(EntityFieldConstants.DA_MODIFIER_GENES + ".id", geneId);
		agmDaParams.put(EntityFieldConstants.WITH_GENE + ".id", geneId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingInteractions(Long geneId) {
		Map<String, Object> interactionParams = new HashMap<>();
		interactionParams.put("query_operator", "or");
		interactionParams.put(EntityFieldConstants.GENE_ASSOCIATION_SUBJECT + ".id", geneId);
		interactionParams.put(EntityFieldConstants.GENE_GENE_ASSOCIATION_OBJECT + ".id", geneId);
		List<Long> results = geneGeneticInteractionDAO.findIdsByParams(interactionParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}
		results = geneMolecularInteractionDAO.findIdsByParams(interactionParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingOrthologyPairs(Long geneId) {
		Map<String, Object> orthologyParams = new HashMap<>();
		orthologyParams.put("query_operator", "or");
		orthologyParams.put(EntityFieldConstants.SUBJECT_GENE + ".id", geneId);
		orthologyParams.put(EntityFieldConstants.OBJECT_GENE + ".id", geneId);
		List<Long> results = geneToGeneOrthologyDAO.findIdsByParams(orthologyParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingParalogyPairs(Long geneId) {
		Map<String, Object> paralogyParams = new HashMap<>();
		paralogyParams.put("query_operator", "or");
		paralogyParams.put(EntityFieldConstants.SUBJECT_GENE + ".id", geneId);
		paralogyParams.put(EntityFieldConstants.OBJECT_GENE + ".id", geneId);
		List<Long> results = geneToGeneParalogyDAO.findIdsByParams(paralogyParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long geneId) {
		Map<String, Object> genePaParams = new HashMap<>();
		genePaParams.put(EntityFieldConstants.PA_SUBJECT + ".id", geneId);
		List<Long> results = genePhenotypeAnnotationDAO.findIdsByParams(genePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put(EntityFieldConstants.ASSERTED_GENES + ".id", geneId);
		agmPaParams.put(EntityFieldConstants.INFERRED_GENE + ".id", geneId);
		results.addAll(agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams));
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put("query_operator", "or");
		allelePaParams.put(EntityFieldConstants.ASSERTED_GENES + ".id", geneId);
		allelePaParams.put(EntityFieldConstants.INFERRED_GENE + ".id", geneId);
		results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingGeneExpressionAnnotations(Long geneId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.EA_SUBJECT + ".id", geneId);
		List<Long> results = geneExpressionAnnotationDAO.findIdsByParams(params);
		return CollectionUtils.isNotEmpty(results);
	}

	public List<String> getAllGenePrimaryExternalIds() {
		String sql = """
			SELECT be.primaryexternalid
			FROM biologicalentity be, gene as g
			WHERE be.id = g.id and be.primaryexternalid is not NULL
		""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();

		objects.forEach(object -> {
			list.add((String) object);
		});

		return list;
	}

	public Map<String, Long> getAllGeneIdsPerSpecies(Species species) {
		String sql = """
						select g.id, be.primaryexternalid, s.displaytext
						from biologicalentity as be, gene as g, slotannotation as s
						where be.taxon_id = :ID
						AND be.id = g.id
						AND s.singlegene_id = g.id
						AND s.slotannotationtype = 'GeneSymbolSlotAnnotation'
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("ID", species.getTaxon().getId());
		List<Object[]> objects = query.getResultList();
		Map<String, Long> ensemblGeneMap = new HashMap<>();
		objects.forEach(object -> {
			ensemblGeneMap.put((String) object[1], (Long) object[0]);
			ensemblGeneMap.put((String) object[2], (Long) object[0]);
		});
		return ensemblGeneMap;
	}

	public List<Long> getAllGeneSummaryIds() {
		String sql = """
				SELECT g.id
				FROM gene g
				INNER JOIN biologicalentity b ON b.id = g.id AND b.obsolete = false AND b.internal = false
				ORDER BY g.id
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public List<Gene> findByIds(List<Long> ids) {

		if (CollectionUtils.isEmpty(ids)) {
			return new ArrayList<>();
		}

		String sql = """
				SELECT g FROM Gene g
				LEFT JOIN FETCH g.geneFullName
				LEFT JOIN FETCH g.dataProvider
				LEFT JOIN FETCH g.dataProviderCrossReference
				WHERE g.id IN :ids
				""";

		return entityManager.createQuery(sql, Gene.class)
			.setParameter("ids", ids)
			.getResultList();
	}

	// --- Batch SQL methods for GeneSearchResultDocument assembly ---

	@SuppressWarnings("unchecked")
	public List<Object[]> getBaseGeneInfo(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT g.id, be.primaryexternalid, fn.formattext, ot_taxon.name,
			       sym.displaytext, so.curie, so.name
			FROM gene g
			JOIN biologicalentity be ON be.id = g.id AND be.obsolete = false AND be.internal = false
			LEFT JOIN slotannotation sym ON sym.singlegene_id = g.id AND sym.slotannotationtype = 'GeneSymbolSlotAnnotation'
			LEFT JOIN slotannotation fn ON fn.singlegene_id = g.id AND fn.slotannotationtype = 'GeneFullNameSlotAnnotation'
			LEFT JOIN ontologyterm ot_taxon ON ot_taxon.id = be.taxon_id
			LEFT JOIN ontologyterm so ON so.id = g.genetype_id
			WHERE g.id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getSoTermAncestors(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT g.id, ancestor_ot.name
			FROM gene g
			JOIN ontologytermclosure otc ON otc.closuresubject_id = g.genetype_id
			JOIN ontologyterm ancestor_ot ON ancestor_ot.id = otc.closureobject_id
			WHERE g.id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGeneSynonyms(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT singlegene_id, displaytext FROM slotannotation
			WHERE slotannotationtype = 'GeneSynonymSlotAnnotation' AND singlegene_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGeneSecondaryIds(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT singlegene_id, secondaryid FROM slotannotation
			WHERE slotannotationtype = 'GeneSecondaryIdSlotAnnotation' AND singlegene_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGeneCrossReferences(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT gc.genomicentity_id, cr.referencedcurie
			FROM genomicentity_crossreference gc
			JOIN crossreference cr ON cr.id = gc.crossreferences_id
			WHERE gc.genomicentity_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGeneChromosomes(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT ggla.geneassociationsubject_id, ac.name
			FROM genegenomiclocationassociation ggla
			JOIN assemblycomponent ac ON ac.id = ggla.genegenomiclocationassociationobject_id
			WHERE ggla.geneassociationsubject_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGeneAlleles(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT aga.allelegeneassociationobject_id, sa.formattext
			FROM allelegeneassociation aga
			JOIN vocabularyterm v ON v.id = aga.relation_id AND v.name = 'is_allele_of'
			JOIN slotannotation sa ON sa.singleallele_id = aga.alleleassociationsubject_id
			  AND sa.slotannotationtype = 'AlleleSymbolSlotAnnotation'
			WHERE aga.allelegeneassociationobject_id IN :geneIds AND aga.internal = false AND aga.obsolete = false
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getGenePhenotypeStatements(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT gpa.phenotypeannotationsubject_id, pa.phenotypeannotationobject
			FROM genephenotypeannotation gpa
			JOIN phenotypeannotation pa ON pa.id = gpa.id
			WHERE gpa.phenotypeannotationsubject_id IN :geneIds
			UNION ALL
			SELECT apa.inferredgene_id, pa.phenotypeannotationobject
			FROM allelephenotypeannotation apa
			JOIN phenotypeannotation pa ON pa.id = apa.id
			WHERE apa.inferredgene_id IN :geneIds
			UNION ALL
			SELECT apg.assertedgenes_id, pa.phenotypeannotationobject
			FROM allelephenotypeannotation_gene apg
			JOIN phenotypeannotation pa ON pa.id = apg.allelephenotypeannotation_id
			WHERE apg.assertedgenes_id IN :geneIds
			UNION ALL
			SELECT agmpa.inferredgene_id, pa.phenotypeannotationobject
			FROM agmphenotypeannotation agmpa
			JOIN phenotypeannotation pa ON pa.id = agmpa.id
			WHERE agmpa.inferredgene_id IN :geneIds
			UNION ALL
			SELECT agmpg.assertedgenes_id, pa.phenotypeannotationobject
			FROM agmphenotypeannotation_gene agmpg
			JOIN phenotypeannotation pa ON pa.id = agmpg.agmphenotypeannotation_id
			WHERE agmpg.assertedgenes_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getDirectGeneDiseases(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT gda.diseaseannotationsubject_id, do_term.name, ancestor_ot.name
			FROM genediseaseannotation gda
			JOIN diseaseannotation da ON da.id = gda.id
			JOIN ontologyterm do_term ON do_term.id = da.diseaseannotationobject_id
			LEFT JOIN ontologytermclosure otc ON otc.closuresubject_id = do_term.id
			LEFT JOIN ontologyterm ancestor_ot ON ancestor_ot.id = otc.closureobject_id
			WHERE gda.diseaseannotationsubject_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getStrictOrthologySymbols(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT o.subjectgene_id, sa.displaytext
			FROM genetogeneorthology o
			JOIN genetogeneorthologygenerated og ON og.id = o.id AND og.strictfilter = true
			JOIN slotannotation sa ON sa.singlegene_id = o.objectgene_id
			  AND sa.slotannotationtype = 'GeneSymbolSlotAnnotation'
			WHERE o.subjectgene_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getOrthologDiseases(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT o.subjectgene_id, do_term.name, ancestor_ot.name
			FROM genetogeneorthology o
			JOIN genediseaseannotation gda ON gda.diseaseannotationsubject_id = o.objectgene_id
			JOIN diseaseannotation da ON da.id = gda.id
			JOIN ontologyterm do_term ON do_term.id = da.diseaseannotationobject_id
			LEFT JOIN ontologytermclosure otc ON otc.closuresubject_id = do_term.id
			LEFT JOIN ontologyterm ancestor_ot ON ancestor_ot.id = otc.closureobject_id
			WHERE o.subjectgene_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getGeneGoTerms(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT goa.singlegene_id, ot.namespace, ot.name,
			       CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM geneontologyannotation goa
			JOIN ontologyterm ot ON ot.id = goa.goterm_id
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ot.id AND ots.subsets = 'goslim_agr'
			WHERE goa.singlegene_id IN :geneIds
			UNION ALL
			SELECT goa.singlegene_id, ot.namespace, ancestor.name,
			       CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM geneontologyannotation goa
			JOIN ontologyterm ot ON ot.id = goa.goterm_id
			JOIN ontologytermclosure otc ON otc.closuresubject_id = ot.id
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ancestor.id AND ots.subsets = 'goslim_agr'
			WHERE goa.singlegene_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getExpressionSubcellularCC(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT gea.expressionannotationsubject_id, cc_term.name,
			       CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM geneexpressionannotation gea
			JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
			JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
			JOIN ontologyterm cc_term ON cc_term.id = ans.cellularcomponentterm_id
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = cc_term.id AND ots.subsets = 'goslim_agr'
			WHERE gea.expressionannotationsubject_id IN :geneIds
			UNION ALL
			SELECT gea.expressionannotationsubject_id, ancestor.name,
			       CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM geneexpressionannotation gea
			JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
			JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
			JOIN ontologyterm cc_term ON cc_term.id = ans.cellularcomponentterm_id
			JOIN ontologytermclosure otc ON otc.closuresubject_id = cc_term.id
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ancestor.id AND ots.subsets = 'goslim_agr'
			WHERE gea.expressionannotationsubject_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public Map<Long, Set<String>> getExpressionAnatomical(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		String sql = """
			SELECT gea.expressionannotationsubject_id, anat_term.name
			FROM geneexpressionannotation gea
			JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
			JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
			JOIN ontologyterm anat_term ON anat_term.id = ans.anatomicalstructure_id
			WHERE gea.expressionannotationsubject_id IN :geneIds
			UNION ALL
			SELECT gea.expressionannotationsubject_id, ancestor.name
			FROM geneexpressionannotation gea
			JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
			JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
			JOIN ontologyterm anat_term ON anat_term.id = ans.anatomicalstructure_id
			JOIN ontologytermclosure otc ON otc.closuresubject_id = anat_term.id
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			WHERE gea.expressionannotationsubject_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return collectToSetMap(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getWhereExpressedAndStages(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT gea.expressionannotationsubject_id, gea.whereexpressedstatement, gea.whenexpressedstagename
			FROM geneexpressionannotation gea
			WHERE gea.expressionannotationsubject_id IN :geneIds
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> getGeneDescriptions(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		String sql = """
			SELECT ben.submittedobject_id, vt.name, n.freetext
			FROM biologicalentity_note ben
			JOIN note n ON n.id = ben.relatednotes_id
			JOIN vocabularyterm vt ON vt.id = n.notetype_id
			WHERE ben.submittedobject_id IN :geneIds
			AND vt.name IN ('automated_gene_description', 'MOD_provided_gene_description')
			""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("geneIds", geneIds);
		return query.getResultList();
	}

	private Map<Long, Set<String>> collectToSetMap(List<Object[]> rows) {
		Map<Long, Set<String>> map = new HashMap<>();
		for (Object[] row : rows) {
			Long id = (Long) row[0];
			String value = (String) row[1];
			if (value != null) {
				map.computeIfAbsent(id, k -> new HashSet<>()).add(value);
			}
		}
		return map;
	}
}
