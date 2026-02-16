package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
}
