package org.alliancegenome.curation_api.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.dao.orthology.GeneToGeneOrthologyDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class GeneDAO extends BaseSQLDAO<Gene> {

	@Inject DataSource dataSource;
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

	public Map<String, Gene> findByIdentifiers(Collection<String> identifiers) {
		if (identifiers == null || identifiers.isEmpty()) {
			return new HashMap<>();
		}
		List<Gene> results = entityManager.createQuery(
				"SELECT g FROM Gene g WHERE g.primaryExternalId IN :ids OR g.modInternalId IN :ids",
				Gene.class)
			.setParameter("ids", identifiers)
			.getResultList();
		Map<String, Gene> map = new HashMap<>();
		for (Gene g : results) {
			if (g.getPrimaryExternalId() != null) {
				map.put(g.getPrimaryExternalId(), g);
			}
			if (g.getModInternalId() != null) {
				map.put(g.getModInternalId(), g);
			}
		}
		return map;
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

	public List<Long> getAllIds() {
		String sql = """
				SELECT g.id
				FROM gene g
				INNER JOIN biologicalentity b ON b.id = g.id AND b.obsolete = false AND b.internal = false
				INNER JOIN species sp ON sp.taxon_id = b.taxon_id
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

	/**
	 * Collects the comprehensive set of AGRKB reference curies (papers) associated with each gene,
	 * unioned across every route by which a gene is tied to a Reference, for the gene_summary doc.
	 * The reference AGRKB curie lives on informationcontententity.curie (Reference extends
	 * InformationContentEntity, not BiologicalEntity), so join informationcontententity for the curie.
	 */
	public Map<Long, Set<String>> getReferencesByGeneIds(List<Long> geneIds) {
		Map<Long, Set<String>> referencesByGene = new HashMap<>();
		if (CollectionUtils.isEmpty(geneIds)) {
			return referencesByGene;
		}

		String referencesQueryString = """
			WITH gene_refs AS (

				-- Route 1: gene is the direct annotation subject (disease/phenotype/expression); paper = the annotation's evidence reference
				SELECT gda.diseaseannotationsubject_id AS gene_id, da.evidenceitem_id AS ref_id
					FROM genediseaseannotation gda JOIN diseaseannotation da ON da.id = gda.id
					WHERE gda.diseaseannotationsubject_id IN :geneIds AND da.evidenceitem_id IS NOT NULL AND da.obsolete = false AND da.internal = false
				UNION SELECT gpa.phenotypeannotationsubject_id, pa.evidenceitem_id
					FROM genephenotypeannotation gpa JOIN phenotypeannotation pa ON pa.id = gpa.id
					WHERE gpa.phenotypeannotationsubject_id IN :geneIds AND pa.evidenceitem_id IS NOT NULL AND pa.obsolete = false AND pa.internal = false
				UNION SELECT gea.expressionannotationsubject_id, gea.evidenceitem_id
					FROM geneexpressionannotation gea
					WHERE gea.expressionannotationsubject_id IN :geneIds AND gea.evidenceitem_id IS NOT NULL AND gea.obsolete = false AND gea.internal = false

				-- Route 2: an allele's annotation rolled up to its gene (inferred or asserted gene); paper = the annotation's evidence reference
				UNION SELECT apa.inferredgene_id, pa.evidenceitem_id
					FROM allelephenotypeannotation apa JOIN phenotypeannotation pa ON pa.id = apa.id
					WHERE apa.inferredgene_id IN :geneIds AND pa.evidenceitem_id IS NOT NULL AND pa.obsolete = false AND pa.internal = false
				UNION SELECT apag.assertedgenes_id, pa.evidenceitem_id
					FROM allelephenotypeannotation_gene apag JOIN phenotypeannotation pa ON pa.id = apag.allelephenotypeannotation_id
					WHERE apag.assertedgenes_id IN :geneIds AND pa.evidenceitem_id IS NOT NULL AND pa.obsolete = false AND pa.internal = false
				UNION SELECT ada.inferredgene_id, da.evidenceitem_id
					FROM allelediseaseannotation ada JOIN diseaseannotation da ON da.id = ada.id
					WHERE ada.inferredgene_id IN :geneIds AND da.evidenceitem_id IS NOT NULL AND da.obsolete = false AND da.internal = false
				UNION SELECT adag.assertedgenes_id, da.evidenceitem_id
					FROM allelediseaseannotation_gene adag JOIN diseaseannotation da ON da.id = adag.allelediseaseannotation_id
					WHERE adag.assertedgenes_id IN :geneIds AND da.evidenceitem_id IS NOT NULL AND da.obsolete = false AND da.internal = false

				-- Route 3: an AGM (model) annotation rolled up to gene (inferred or asserted gene); paper = the annotation's evidence reference
				UNION SELECT apa.inferredgene_id, pa.evidenceitem_id
					FROM agmphenotypeannotation apa JOIN phenotypeannotation pa ON pa.id = apa.id
					WHERE apa.inferredgene_id IN :geneIds AND pa.evidenceitem_id IS NOT NULL AND pa.obsolete = false AND pa.internal = false
				UNION SELECT apag.assertedgenes_id, pa.evidenceitem_id
					FROM agmphenotypeannotation_gene apag JOIN phenotypeannotation pa ON pa.id = apag.agmphenotypeannotation_id
					WHERE apag.assertedgenes_id IN :geneIds AND pa.evidenceitem_id IS NOT NULL AND pa.obsolete = false AND pa.internal = false
				UNION SELECT ada.inferredgene_id, da.evidenceitem_id
					FROM agmdiseaseannotation ada JOIN diseaseannotation da ON da.id = ada.id
					WHERE ada.inferredgene_id IN :geneIds AND da.evidenceitem_id IS NOT NULL AND da.obsolete = false AND da.internal = false
				UNION SELECT adag.assertedgenes_id, da.evidenceitem_id
					FROM agmdiseaseannotation_gene adag JOIN diseaseannotation da ON da.id = adag.agmdiseaseannotation_id
					WHERE adag.assertedgenes_id IN :geneIds AND da.evidenceitem_id IS NOT NULL AND da.obsolete = false AND da.internal = false

				-- Route 4: papers linked directly to the gene's alleles (allele_reference), rolled up through allelegeneassociation
				UNION SELECT aga.allelegeneassociationobject_id, ar.references_id
					FROM allelegeneassociation aga
					JOIN allele_reference ar ON ar.allele_id = aga.alleleassociationsubject_id
					WHERE aga.allelegeneassociationobject_id IN :geneIds AND aga.obsolete = false AND aga.internal = false
			)
			SELECT DISTINCT gr.gene_id, ice.curie
			FROM gene_refs gr
			JOIN reference r ON r.id = gr.ref_id
			JOIN informationcontententity ice ON ice.id = r.id
			WHERE ice.curie IS NOT NULL
			""";
		Query referencesQuery = entityManager.createNativeQuery(referencesQueryString);
		referencesQuery.setParameter("geneIds", geneIds);
		List<Object[]> referencesResults = referencesQuery.getResultList();

		for (Object[] row : referencesResults) {
			Long geneId = ((Number) row[0]).longValue();
			String referenceCurie = (String) row[1];
			referencesByGene.computeIfAbsent(geneId, key -> new HashSet<>()).add(referenceCurie);
		}
		return referencesByGene;
	}

	// --- Batch SQL methods for GeneSearchResultDocument assembly ---

	public List<Object[]> getBaseGeneInfo(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			SELECT g.id, be.primaryexternalid, fn.formattext, sp.fullname, sp.abbreviation,
				sym.displaytext, so.curie, so.name
			FROM gene g
			JOIN biologicalentity be ON be.id = g.id AND be.obsolete = false AND be.internal = false
			LEFT JOIN slotannotation sym ON sym.singlegene_id = g.id AND sym.slotannotationtype = 'GeneSymbolSlotAnnotation'
			LEFT JOIN slotannotation fn ON fn.singlegene_id = g.id AND fn.slotannotationtype = 'GeneFullNameSlotAnnotation'
			LEFT JOIN ontologyterm ot_taxon ON ot_taxon.id = be.taxon_id
			LEFT JOIN species sp ON sp.taxon_id = ot_taxon.id
			LEFT JOIN ontologyterm so ON so.id = g.genetype_id
			WHERE g.id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getSoTermAncestors(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT g.id, ancestor_ot.name
			FROM gene g
			JOIN ontologytermclosure otc ON otc.closuresubject_id = g.genetype_id
			JOIN ontologyterm ancestor_ot ON ancestor_ot.id = otc.closureobject_id AND ancestor_ot.ontologytermtype = 'SOTerm'
			WHERE g.id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneSynonyms(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT singlegene_id, displaytext FROM slotannotation
			WHERE slotannotationtype = 'GeneSynonymSlotAnnotation' AND singlegene_id IN :geneIds
			""", geneIds);
	}

	public Map<Long, String> getGeneSystematicNames(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		List<Object[]> rows = runJdbcQuery("""
			SELECT singlegene_id, displaytext FROM slotannotation
			WHERE slotannotationtype = 'GeneSystematicNameSlotAnnotation' AND singlegene_id IN :geneIds
			""", geneIds);
		Map<Long, String> result = new HashMap<>();
		for (Object[] row : rows) {
			Long id = (Long) row[0];
			String name = (String) row[1];
			if (name != null) {
				result.put(id, name);
			}
		}
		return result;
	}

	public Map<Long, Set<String>> getGeneSecondaryIds(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT singlegene_id, secondaryid FROM slotannotation
			WHERE slotannotationtype = 'GeneSecondaryIdSlotAnnotation' AND singlegene_id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneCrossReferences(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT gc.genomicentity_id, cr.referencedcurie
			FROM genomicentity_crossreference gc
			JOIN crossreference cr ON cr.id = gc.crossreferences_id
			WHERE gc.genomicentity_id IN :geneIds
			UNION
			SELECT g.id, cr.referencedcurie
			FROM gene g
			JOIN crossreference cr ON cr.id = g.gcrpcrossreference_id
			WHERE g.id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneChromosomes(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT ggla.geneassociationsubject_id,
				CASE WHEN ac.name LIKE 'chr%' THEN substring(ac.name, 4) ELSE ac.name END
			FROM genegenomiclocationassociation ggla
			JOIN assemblycomponent ac ON ac.id = ggla.genegenomiclocationassociationobject_id
			WHERE ggla.geneassociationsubject_id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneAlleles(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT aga.allelegeneassociationobject_id, sa.formattext
			FROM allelegeneassociation aga
			JOIN vocabularyterm v ON v.id = aga.relation_id AND v.name = 'is_allele_of'
			JOIN slotannotation sa ON sa.singleallele_id = aga.alleleassociationsubject_id
				AND sa.slotannotationtype = 'AlleleSymbolSlotAnnotation'
			WHERE aga.allelegeneassociationobject_id IN :geneIds AND aga.internal = false AND aga.obsolete = false
			""", geneIds);
	}

	public Map<Long, Set<String>> getGenePhenotypeStatements(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
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
			""", geneIds);
	}

	public List<Object[]> getDirectGeneDiseases(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			SELECT gda.diseaseannotationsubject_id, do_term.name, ancestor_ot.name
			FROM genediseaseannotation gda
			JOIN diseaseannotation da ON da.id = gda.id
			JOIN ontologyterm do_term ON do_term.id = da.diseaseannotationobject_id
			LEFT JOIN ontologytermclosure otc ON otc.closuresubject_id = do_term.id
			LEFT JOIN ontologyterm ancestor_ot ON ancestor_ot.id = otc.closureobject_id
				AND ancestor_ot.ontologytermtype = 'DOTerm'
			WHERE gda.diseaseannotationsubject_id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneDiseaseAgrSlim(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT gene_id, ancestor.name
			FROM (
				SELECT DISTINCT gda.diseaseannotationsubject_id AS gene_id, da.diseaseannotationobject_id AS doterm_id
				FROM genediseaseannotation gda
				JOIN diseaseannotation da ON da.id = gda.id
				WHERE gda.diseaseannotationsubject_id IN :geneIds
				UNION
				SELECT DISTINCT o.subjectgene_id AS gene_id, da.diseaseannotationobject_id AS doterm_id
				FROM genetogeneorthology o
				JOIN genetogeneorthologygenerated og ON og.id = o.id AND og.strictfilter = true
				JOIN genediseaseannotation gda ON gda.diseaseannotationsubject_id = o.objectgene_id
				JOIN diseaseannotation da ON da.id = gda.id
				WHERE o.subjectgene_id IN :geneIds
			) gene_diseases
			JOIN ontologytermclosure otc ON otc.closuresubject_id = gene_diseases.doterm_id
			JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			JOIN ontologyterm_subsets sub ON sub.ontologyterm_id = ancestor.id
			WHERE sub.subsets = 'DO_AGR_slim'
			""", geneIds);
	}

	public Map<Long, Set<String>> getStrictOrthologySymbols(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT o.subjectgene_id, sa.displaytext
			FROM genetogeneorthology o
			JOIN genetogeneorthologygenerated og ON og.id = o.id AND og.strictfilter = true
			JOIN slotannotation sa ON sa.singlegene_id = o.objectgene_id
				AND sa.slotannotationtype = 'GeneSymbolSlotAnnotation'
			WHERE o.subjectgene_id IN :geneIds
			""", geneIds);
	}

	public List<Object[]> getOrthologDiseases(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			WITH ortho_diseases AS MATERIALIZED (
				SELECT o.subjectgene_id, da.diseaseannotationobject_id AS term_id
				FROM genetogeneorthology o
				JOIN genediseaseannotation gda ON gda.diseaseannotationsubject_id = o.objectgene_id
				JOIN diseaseannotation da ON da.id = gda.id
				WHERE o.subjectgene_id IN :geneIds
			),
			distinct_ortho_disease_terms AS MATERIALIZED (
				SELECT DISTINCT term_id FROM ortho_diseases
			),
			ortho_disease_ancestors AS MATERIALIZED (
				SELECT dodt.term_id, ancestor.name
				FROM distinct_ortho_disease_terms dodt
				JOIN ontologytermclosure otc ON otc.closuresubject_id = dodt.term_id
				JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id AND ancestor.ontologytermtype = 'DOTerm'
			)
			SELECT od.subjectgene_id, do_term.name, oda.name
			FROM ortho_diseases od
			JOIN ontologyterm do_term ON do_term.id = od.term_id
			LEFT JOIN ortho_disease_ancestors oda ON oda.term_id = od.term_id
			""", geneIds);
	}

	public List<Object[]> getGeneGoTerms(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			WITH gene_go AS MATERIALIZED (
				SELECT goa.singlegene_id, goa.goterm_id
				FROM geneontologyannotation goa
				WHERE goa.singlegene_id IN :geneIds
			),
			distinct_go_terms AS MATERIALIZED (
				SELECT DISTINCT goterm_id AS term_id FROM gene_go
			),
			go_ancestors AS MATERIALIZED (
				SELECT dgt.term_id, ancestor.id AS ancestor_id, ancestor.name,
					CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END AS is_agr_slim
				FROM distinct_go_terms dgt
				JOIN ontologytermclosure otc ON otc.closuresubject_id = dgt.term_id
				JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id AND ancestor.ontologytermtype = 'GOTerm'
				LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ancestor.id AND ots.subsets = 'goslim_agr'
			)
			SELECT gg.singlegene_id, ot.namespace, ot.name,
				CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM gene_go gg
			JOIN ontologyterm ot ON ot.id = gg.goterm_id AND ot.ontologytermtype = 'GOTerm'
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ot.id AND ots.subsets = 'goslim_agr'
			UNION ALL
			SELECT gg.singlegene_id, ot.namespace, ga.name, ga.is_agr_slim
			FROM gene_go gg
			JOIN ontologyterm ot ON ot.id = gg.goterm_id AND ot.ontologytermtype = 'GOTerm'
			JOIN go_ancestors ga ON ga.term_id = gg.goterm_id
			""", geneIds);
	}

	public List<Object[]> getExpressionSubcellularCC(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			WITH gene_cc AS MATERIALIZED (
				SELECT gea.expressionannotationsubject_id AS gene_id, ans.cellularcomponentterm_id AS term_id
				FROM geneexpressionannotation gea
				JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
				JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
				WHERE gea.expressionannotationsubject_id IN :geneIds
				AND ans.cellularcomponentterm_id IS NOT NULL
			),
			distinct_cc_terms AS MATERIALIZED (
				SELECT DISTINCT term_id FROM gene_cc
			),
			cc_ancestors AS MATERIALIZED (
				SELECT dct.term_id, ancestor.name,
					CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END AS is_agr_slim
				FROM distinct_cc_terms dct
				JOIN ontologytermclosure otc ON otc.closuresubject_id = dct.term_id
				JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id AND ancestor.ontologytermtype = 'GOTerm'
				LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = ancestor.id AND ots.subsets = 'goslim_agr'
			)
			SELECT gcc.gene_id, cc_term.name,
				CASE WHEN ots.ontologyterm_id IS NOT NULL THEN true ELSE false END
			FROM gene_cc gcc
			JOIN ontologyterm cc_term ON cc_term.id = gcc.term_id AND cc_term.ontologytermtype = 'GOTerm'
			LEFT JOIN ontologyterm_subsets ots ON ots.ontologyterm_id = cc_term.id AND ots.subsets = 'goslim_agr'
			UNION ALL
			SELECT gcc.gene_id, ca.name, ca.is_agr_slim
			FROM gene_cc gcc
			JOIN cc_ancestors ca ON ca.term_id = gcc.term_id
			""", geneIds);
	}

	public Map<Long, Set<String>> getExpressionAnatomical(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			WITH gene_anat AS MATERIALIZED (
				SELECT gea.expressionannotationsubject_id AS gene_id, ans.anatomicalstructure_id AS term_id
				FROM geneexpressionannotation gea
				JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
				JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
				WHERE gea.expressionannotationsubject_id IN :geneIds
				AND ans.anatomicalstructure_id IS NOT NULL
			),
			distinct_anat_terms AS MATERIALIZED (
				SELECT DISTINCT dat.term_id, ot.name
				FROM (SELECT DISTINCT term_id FROM gene_anat) dat
				JOIN ontologyterm ot ON ot.id = dat.term_id
			),
			anat_ancestors AS MATERIALIZED (
				SELECT dat.term_id, ancestor.name
				FROM distinct_anat_terms dat
				JOIN ontologytermclosure otc ON otc.closuresubject_id = dat.term_id
				JOIN ontologyterm ancestor ON ancestor.id = otc.closureobject_id
			)
			SELECT ga.gene_id, dat.name
			FROM gene_anat ga
			JOIN distinct_anat_terms dat ON dat.term_id = ga.term_id
			UNION ALL
			SELECT ga.gene_id, aa.name
			FROM gene_anat ga
			JOIN anat_ancestors aa ON aa.term_id = ga.term_id
			""", geneIds);
	}

	public Map<Long, Set<String>> getExpressionAnatomicalSlim(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT gea.expressionannotationsubject_id, ot.name
			FROM geneexpressionannotation gea
			JOIN expressionpattern ep ON ep.id = gea.expressionpattern_id
			JOIN anatomicalsite ans ON ans.id = ep.whereexpressed_id
			JOIN anatomicalsite_anatomicalstructureuberonterms asut
				ON asut.anatomicalsite_id = ans.id
			JOIN ontologyterm ot ON ot.id = asut.anatomicalstructureuberonterms_id
			WHERE gea.expressionannotationsubject_id IN :geneIds
			""", geneIds);
	}

	public List<Object[]> getWhereExpressedAndStages(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			SELECT gea.expressionannotationsubject_id, gea.whereexpressedstatement, gea.whenexpressedstagename
			FROM geneexpressionannotation gea
			WHERE gea.expressionannotationsubject_id IN :geneIds
			""", geneIds);
	}

	public Map<Long, Set<String>> getGeneModels(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new HashMap<>();
		}
		return runJdbcSetQuery("""
			SELECT ag.allelegeneassociationobject_id, slag.formattext
			FROM allelegeneassociation ag
			JOIN allele a ON a.id = ag.alleleassociationsubject_id
			JOIN agmalleleassociation aa ON aa.agmalleleassociationobject_id = a.id
			JOIN affectedgenomicmodel agm ON agm.id = aa.agmassociationsubject_id
			JOIN slotannotation slag ON slag.singleagm_id = agm.id AND slag.slotannotationtype = 'AgmFullNameSlotAnnotation'
			WHERE ag.allelegeneassociationobject_id IN :geneIds
			AND ag.internal = false AND ag.obsolete = false
			AND aa.internal = false AND aa.obsolete = false
			""", geneIds);
	}

	public List<Object[]> getGeneDescriptions(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}
		return runJdbcQuery("""
			SELECT ben.submittedobject_id, vt.name, n.freetext
			FROM biologicalentity_note ben
			JOIN note n ON n.id = ben.relatednotes_id
			JOIN vocabularyterm vt ON vt.id = n.notetype_id
			WHERE ben.submittedobject_id IN :geneIds
			AND vt.name IN ('automated_gene_description', 'MOD_provided_gene_description')
			""", geneIds);
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

	// --- Thread-safe JDBC query helpers (bypass request-scoped EntityManager) ---

	public List<Object[]> runJdbcQuery(String sql, List<Long> geneIds) {
		String expandedSql = sql.replace(":geneIds", buildInClause(geneIds));
		List<Object[]> results = new ArrayList<>();
		try (Connection conn = dataSource.getConnection();
				PreparedStatement ps = conn.prepareStatement(expandedSql);
				ResultSet rs = ps.executeQuery()) {
			int colCount = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				Object[] row = new Object[colCount];
				for (int i = 0; i < colCount; i++) {
					row[i] = rs.getObject(i + 1);
				}
				results.add(row);
			}
		} catch (SQLException e) {
			throw new RuntimeException("JDBC query failed: " + e.getMessage(), e);
		}
		return results;
	}

	public Map<Long, Set<String>> runJdbcSetQuery(String sql, List<Long> geneIds) {
		return collectToSetMap(runJdbcQuery(sql, geneIds));
	}

	private String buildInClause(List<Long> ids) {
		StringBuilder sb = new StringBuilder("(");
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(ids.get(i));
		}
		sb.append(")");
		return sb.toString();
	}
}
