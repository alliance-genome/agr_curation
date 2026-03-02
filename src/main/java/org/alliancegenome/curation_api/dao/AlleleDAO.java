package org.alliancegenome.curation_api.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.AgmAlleleAssociationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.document.es.AlleleSummaryDTO;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.PredictedVariantConsequence;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.associations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.associations.TranscriptGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSymbolSlotAnnotation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Query;

@ApplicationScoped
public class AlleleDAO extends BaseSQLDAO<Allele> {

	@Inject
	GeneDiseaseAnnotationDAO geneDiseaseAnnotationDAO;
	@Inject
	AlleleDiseaseAnnotationDAO alleleDiseaseAnnotationDAO;
	@Inject
	AGMDiseaseAnnotationDAO agmDiseaseAnnotationDAO;
	@Inject
	AllelePhenotypeAnnotationDAO allelePhenotypeAnnotationDAO;
	@Inject
	AGMPhenotypeAnnotationDAO agmPhenotypeAnnotationDAO;
	@Inject
	AgmAlleleAssociationDAO agmAlleleAssociationDAO;
	@Inject
	HTPExpressionDatasetSampleAnnotationDAO htpExpressionDatasetSampleAnnotationDAO;

	protected AlleleDAO() {
		super(Allele.class);
	}

	public Boolean hasReferencingDiseaseAnnotations(Long alleleId) {

		Map<String, Object> alleleDaParams = new HashMap<>();
		alleleDaParams.put("query_operator", "or");
		alleleDaParams.put(EntityFieldConstants.DA_SUBJECT + ".id", alleleId);
		alleleDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		List<Long> results = alleleDiseaseAnnotationDAO.findIdsByParams(alleleDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmDaParams = new HashMap<>();
		agmDaParams.put("query_operator", "or");
		agmDaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		agmDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = agmDiseaseAnnotationDAO.findIdsByParams(agmDaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> geneDaParams = new HashMap<>();
		geneDaParams.put(EntityFieldConstants.DA_MODIFIER_ALLELES + ".id", alleleId);
		results = geneDiseaseAnnotationDAO.findIdsByParams(geneDaParams);

		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingPhenotypeAnnotations(Long alleleId) {

		Map<String, Object> allelePaParams = new HashMap<>();
		allelePaParams.put(EntityFieldConstants.PA_SUBJECT + ".id", alleleId);
		List<Long> results = allelePhenotypeAnnotationDAO.findIdsByParams(allelePaParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> agmPaParams = new HashMap<>();
		agmPaParams.put("query_operator", "or");
		agmPaParams.put(EntityFieldConstants.ASSERTED_ALLELES + ".id", alleleId);
		agmPaParams.put(EntityFieldConstants.INFERRED_ALLELE + ".id", alleleId);
		results = agmPhenotypeAnnotationDAO.findIdsByParams(agmPaParams);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingAgmAlleleAssociations(Long alleleId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.AGM_ALLELE_ASSOCIATION_OBJECT + ".id", alleleId);
		List<Long> results = agmAlleleAssociationDAO.findIdsByParams(params);
		return CollectionUtils.isNotEmpty(results);
	}

	public Boolean hasReferencingHTPExpressionDatasetSampleAnnotation(Long alleleId) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.GENOMIC_INFORMATION_ALLELE + ".id", alleleId);
		List<Long> results = htpExpressionDatasetSampleAnnotationDAO.findIdsByParams(params);

		return CollectionUtils.isNotEmpty(results);
	}

	public List<String> getAllAllelePrimaryExternalIds() {
		String sql = """
					SELECT be.primaryexternalid
					FROM biologicalentity be, allele as a
					WHERE be.id = a.id and be.primaryexternalid is not NULL
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> objects = query.getResultList();
		List<String> list = new ArrayList<>();

		objects.forEach(object -> {
			list.add((String) object);
		});

		return list;
	}

	public SearchResponse<AlleleSummaryDTO> findAllelesForSummary(Pagination pagination, Map<String, Object> params) {

		String baseCountQuery = """
				SELECT count( a.id)
				""";
		String baseSelectQuery = """
				SELECT
				a.id,
				b.primaryExternalId,
				b.curie,
				b.modInternalId,
				s.formatText,
				s.displayText,
				ot.curie,
				ot.name,
				cr.referencedCurie,
				cr.displayName,
				rd.name,
				rd.urltemplate,
				org.id,
				org.abbreviation,
				org.fullname,
				org.shortname
				""";
		// Build cursor condition for optimization
		String cursorCondition = "";
		boolean useCursorCondition = false;
		if (pagination.getCursor() != null) {
			cursorCondition = " AND a.id > :cursor";
			useCursorCondition = true;
		}

		String baseQuery = """
				FROM Allele a
				INNER JOIN BiologicalEntity b ON b.id = a.id AND b.obsolete = false AND b.internal = false
				INNER JOIN SlotAnnotation s ON a.id = s.singleallele_id
					AND s.slotannotationtype = 'AlleleSymbolSlotAnnotation'
					AND s.formatText IS NOT NULL
				INNER JOIN OntologyTerm ot ON ot.id = b.taxon_id
				INNER JOIN CrossReference cr ON cr.id = b.dataprovidercrossreference_id
				INNER JOIN resourceDescriptorPage rd ON rd.id = cr.resourcedescriptorpage_id
				INNER JOIN organization org ON org.id = b.dataprovider_id
				""" + cursorCondition;

		Query query;
		if (pagination.getPage() == 0 && pagination.getLimit() == 0) {
			query = entityManager.createNativeQuery(baseCountQuery + baseQuery);
			if (useCursorCondition) {
				query.setParameter("cursor", pagination.getCursor());
			}
			SearchResponse<AlleleSummaryDTO> emptyResponse = new SearchResponse<>();
			emptyResponse.setResults(new ArrayList<>());
			Long totalCount = (Long) query.getSingleResult();
			emptyResponse.setTotalResults(totalCount);
			return emptyResponse;
		} else {
			query = entityManager.createNativeQuery(baseSelectQuery + baseQuery + " ORDER BY a.id");

			if (useCursorCondition) {
				query.setParameter("cursor", pagination.getCursor());
			}

			// Use cursor-based pagination if cursor is provided, otherwise fall back to offset
			if (pagination.getCursor() != null) {
				// For cursor-based pagination, we don't need OFFSET
				query.setMaxResults(pagination.getLimit());
			} else {
				// Traditional offset-based pagination
				query.setFirstResult(pagination.getPage() * pagination.getLimit());
				query.setMaxResults(pagination.getLimit());
			}
		}

		List<Object[]> results = query.getResultList();

		List<Allele> alleles = buildAllelesFromResults(results);

		List<AlleleSummaryDTO> dtos = enrichAllelesAndBuildDTOs(alleles);

		SearchResponse<AlleleSummaryDTO> response = new SearchResponse<>();
		response.setResults(dtos);
		response.setTotalResults((long) alleles.size());

		// Set nextCursor for cursor-based pagination
		if (!alleles.isEmpty() && pagination.getCursor() != null) {
			Long lastId = alleles.get(alleles.size() - 1).getId();
			response.setNextCursor(lastId);
		}

		return response;
	}

	public List<Long> getAllAlleleSummaryIds() {
		String sql = """
				SELECT a.id
				FROM Allele a
				INNER JOIN BiologicalEntity b ON b.id = a.id AND b.obsolete = false AND b.internal = false
				ORDER BY a.id
				""";

		Query query = entityManager.createNativeQuery(sql);
		List<Object> results = query.getResultList();
		return results.stream().map(obj -> (Long) obj).collect(Collectors.toList());
	}

	public SearchResponse<AlleleSummaryDTO> findAllelesForSummaryByIds(List<Long> alleleIds) {
		if (CollectionUtils.isEmpty(alleleIds)) {
			SearchResponse<AlleleSummaryDTO> emptyResponse = new SearchResponse<>();
			emptyResponse.setTotalResults(0L);
			return emptyResponse;
		}

		String selectQuery = """
				SELECT
				a.id,
				b.primaryExternalId,
				b.curie,
				b.modInternalId,
				s.formatText,
				s.displayText,
				ot.curie,
				ot.name,
				cr.referencedCurie,
				cr.displayName,
				rd.name,
				rd.urltemplate,
				org.id,
				org.abbreviation,
				org.fullname,
				org.shortname
				FROM Allele a
				INNER JOIN BiologicalEntity b ON b.id = a.id
				INNER JOIN SlotAnnotation s ON a.id = s.singleallele_id
					AND s.slotannotationtype = 'AlleleSymbolSlotAnnotation'
					AND s.formatText IS NOT NULL
				INNER JOIN OntologyTerm ot ON ot.id = b.taxon_id
				INNER JOIN CrossReference cr ON cr.id = b.dataprovidercrossreference_id
				INNER JOIN resourceDescriptorPage rd ON rd.id = cr.resourcedescriptorpage_id
				INNER JOIN organization org ON org.id = b.dataprovider_id
				WHERE a.id IN :alleleIds
				""";

		Query query = entityManager.createNativeQuery(selectQuery);
		query.setParameter("alleleIds", alleleIds);
		List<Object[]> results = query.getResultList();

		List<Allele> alleles = buildAllelesFromResults(results);

		List<AlleleSummaryDTO> dtos = enrichAllelesAndBuildDTOs(alleles);

		SearchResponse<AlleleSummaryDTO> response = new SearchResponse<>();
		response.setResults(dtos);
		response.setTotalResults((long) alleles.size());
		return response;
	}

	private List<Allele> buildAllelesFromResults(List<Object[]> results) {
		List<Allele> alleles = new ArrayList<>();
		for (Object[] row : results) {
			Allele allele = new Allele();
			allele.setId((Long) row[0]);
			allele.setPrimaryExternalId((String) row[1]);
			allele.setCurie((String) row[2]);
			allele.setModInternalId((String) row[3]);

			if (row[4] != null || row[5] != null) {
				AlleleSymbolSlotAnnotation symbolAnnotation = new AlleleSymbolSlotAnnotation();
				symbolAnnotation.setFormatText((String) row[4]);
				symbolAnnotation.setDisplayText((String) row[5]);
				allele.setAlleleSymbol(symbolAnnotation);
			}
			if (row[6] != null || row[7] != null) {
				NCBITaxonTerm term = new NCBITaxonTerm();
				term.setCurie((String) row[6]);
				term.setName((String) row[7]);
				allele.setTaxon(term);
			}
			if (row[8] != null || row[9] != null) {
				CrossReference term = new CrossReference();
				term.setReferencedCurie((String) row[8]);
				term.setDisplayName((String) row[9]);
				allele.setDataProviderCrossReference(term);
				if (row[10] != null || row[11] != null) {
					ResourceDescriptorPage page = new ResourceDescriptorPage();
					page.setName((String) row[10]);
					page.setUrlTemplate((String) row[11]);
					term.setResourceDescriptorPage(page);
				}
			}
			if (row[12] != null) {
				Organization org = new Organization();
				org.setId((Long) row[12]);
				org.setAbbreviation((String) row[13]);
				org.setFullName((String) row[14]);
				org.setShortName((String) row[15]);
				allele.setDataProvider(org);
			}
			alleles.add(allele);
		}
		return alleles;
	}

	private List<AlleleSummaryDTO> enrichAllelesAndBuildDTOs(List<Allele> alleles) {
		List<Long> alleleIds = alleles.stream()
				.map(Allele::getId)
				.collect(Collectors.toList());

		Map<Long, Allele> alleleMap = alleles.stream().collect(Collectors.toMap(Allele::getId, Function.identity()));

		// Synonyms
		String synonymQueryString = """
				select singleallele_id, string_agg(displaytext,'||'), string_agg(formatText,'||')
				from slotannotation
				where slotannotationtype = 'AlleleSynonymSlotAnnotation'
				and singleallele_id in :alleleIds
				group by singleallele_id
								""";
		Query synonymQuery = entityManager.createNativeQuery(synonymQueryString);
		synonymQuery.setParameter("alleleIds", alleleIds);
		List<Object[]> synonymQueryResults = synonymQuery.getResultList();
		synonymQueryResults.forEach(objects -> {
			String synonymList = (String) objects[1];
			String synonymListFormat = (String) objects[2];
			if (synonymList != null) {
				String[] synonymArray = synonymList.split("\\|\\|");
				String[] synonymArrayFormat = synonymListFormat.split("\\|\\|");
				AtomicInteger index = new AtomicInteger(0);
				List<AlleleSynonymSlotAnnotation> slotList = Arrays.stream(synonymArray).
						map(synonym -> {
							AlleleSynonymSlotAnnotation slot = new AlleleSynonymSlotAnnotation();
							slot.setDisplayText(synonym);
							slot.setFormatText(synonymArrayFormat[index.get()]);
							index.incrementAndGet();
							return slot;
						}).toList();
				alleleMap.get(objects[0]).setAlleleSynonyms(slotList);
			}
		});

		// Gene associations
		String geneAssociationInfoQuery = """
				select alleleassociationsubject_id as allele_id,
					allelegeneassociationobject_id as gene_id,
					be.primaryexternalid as gene_primary_id,
					sa.displaytext as gene_symbol,
					sa.formattext as gene_symbol_format,
					ot.name as taxon_nameagain
				from allelegeneassociation aga
					join vocabularyterm v on v.id = aga.relation_id
					join gene g on g.id = aga.allelegeneassociationobject_id
					join biologicalentity be on be.id = g.id
					join slotannotation sa on sa.singlegene_id = g.id and sa.slotannotationtype = 'GeneSymbolSlotAnnotation'
					join OntologyTerm ot on ot.id=be.taxon_id
				where alleleassociationsubject_id in :alleleIds
				and aga.internal = false
				and aga.obsolete = false
				and v.name = 'is_allele_of';
				""";

		Query geneAssociationQuery = entityManager.createNativeQuery(geneAssociationInfoQuery);
		geneAssociationQuery.setParameter("alleleIds", alleleIds);
		List<Object[]> geneAssociationResults = geneAssociationQuery.getResultList();

		List<AlleleGeneAssociation> associationList =
				geneAssociationResults.stream().map(objects -> {
					Long alleleID = (Long) objects[0];
					Allele allele = alleleMap.get(alleleID);
					AlleleGeneAssociation association = new AlleleGeneAssociation();
					association.setAlleleAssociationSubject(allele);
					VocabularyTerm term = new VocabularyTerm();
					term.setName("is_allele_of");
					association.setRelation(term);
					Gene gene = new Gene();
					gene.setId((Long) objects[1]);
					gene.setPrimaryExternalId((String) objects[2]);

					GeneSymbolSlotAnnotation annotation = new GeneSymbolSlotAnnotation();
					annotation.setDisplayText((String) objects[3]);
					annotation.setFormatText((String) objects[4]);
					gene.setGeneSymbol(annotation);
					association.setAlleleGeneAssociationObject(gene);
					allele.setAlleleGeneAssociations(List.of(association));
					return association;
				}).toList();

		Map<Allele, List<AlleleGeneAssociation>> map = associationList.stream().collect(Collectors.groupingBy(AlleleGeneAssociation::getAlleleAssociationSubject));
		map.forEach((mapAllele, alleleGeneAssociations) -> {
			Allele allele = alleleMap.get(mapAllele.getId());
			allele.setAlleleGeneAssociations(alleleGeneAssociations);
		});

		// Variants
		String variantQueryString = """
			SELECT DISTINCT ava.alleleassociationsubject_id as allele_id,
				v.id as variant_id,
				o.name as variant_type,
				cvg.hgvs,
				cvg.start,
				cvg.end,
				ac.name as chromosome,
				otc.name as consequence,
				pvc.id as pvc_id,
				vt_impact.name as vep_impact,
				vt_sift.name as sift_prediction,
				pvc.siftscore,
				vt_poly.name as polyphen_prediction,
				pvc.polyphenscore,
				t.name as transcript_name,
				be_t.primaryexternalid as transcript_id,
				be_t.curie as transcript_curie,
				ot_tt.name as transcript_type,
				pvc.genelevelconsequence,
				be_g.primaryexternalid as gene_id,
				cvg.id as cvg_id,
				sa_g.displaytext as gene_symbol,
				sa_g.formattext as gene_symbol_format
			FROM allelevariantassociation ava
				JOIN variant v ON v.id = ava.allelevariantassociationobject_id
				JOIN ontologyterm o ON o.id = v.varianttype_id
				JOIN curatedvariantgenomiclocation cvg ON cvg.variantassociationsubject_id = v.id
				JOIN assemblycomponent ac ON cvg.variantgenomiclocationassociationobject_id = ac.id
				LEFT JOIN predictedvariantconsequence pvc ON pvc.variantgenomiclocation_id = cvg.id
				LEFT JOIN predictedvariantconsequence_ontologyterm pvco ON pvco.predictedvariantconsequence_id = pvc.id
				LEFT JOIN ontologyterm otc ON otc.id = pvco.vepconsequences_id
				LEFT JOIN vocabularyterm vt_impact ON vt_impact.id = pvc.vepimpact_id
				LEFT JOIN vocabularyterm vt_sift ON vt_sift.id = pvc.siftprediction_id
				LEFT JOIN vocabularyterm vt_poly ON vt_poly.id = pvc.polyphenprediction_id
				LEFT JOIN transcript t ON t.id = pvc.varianttranscript_id
				LEFT JOIN biologicalentity be_t ON be_t.id = t.id
				LEFT JOIN ontologyterm ot_tt ON ot_tt.id = t.transcripttype_id
				LEFT JOIN transcriptgeneassociation tga ON tga.transcriptassociationsubject_id = t.id
				LEFT JOIN biologicalentity be_g ON be_g.id = tga.transcriptgeneassociationobject_id
				LEFT JOIN slotannotation sa_g ON sa_g.singlegene_id = be_g.id AND sa_g.slotannotationtype = 'GeneSymbolSlotAnnotation'
				WHERE ava.alleleassociationsubject_id IN :alleleIds
			AND ava.obsolete = false AND ava.internal = false
			""";

		Query variantQuery = entityManager.createNativeQuery(variantQueryString);
		variantQuery.setParameter("alleleIds", alleleIds);
		List<Object[]> variantResults = variantQuery.getResultList();

		Map<Long, Map<Long, Variant>> alleleVariantMap = new HashMap<>();

		for (Object[] row : variantResults) {
			Long alleleId = (Long) row[0];
			Long variantId = (Long) row[1];
			Long pvcId = (Long) row[8];
			Long cvgId = (Long) row[20];

			Map<Long, Variant> variantMap = alleleVariantMap.computeIfAbsent(alleleId, k -> new HashMap<>());
			Variant variant = variantMap.get(variantId);
			if (variant == null) {
				variant = new Variant();
				variant.setId(variantId);
				SOTerm variantType = new SOTerm();
				variantType.setName((String) row[2]);
				variant.setVariantType(variantType);
				variant.setCuratedVariantGenomicLocations(new ArrayList<>());
				variantMap.put(variantId, variant);
			}

			// Find or create the genomic location
			CuratedVariantGenomicLocationAssociation cvgla = variant.getCuratedVariantGenomicLocations().stream()
				.filter(c -> cvgId.equals(c.getId())).findFirst().orElse(null);
			if (cvgla == null) {
				cvgla = new CuratedVariantGenomicLocationAssociation();
				cvgla.setId(cvgId);
				cvgla.setHgvs((String) row[3]);
				cvgla.setStart((Integer) row[4]);
				cvgla.setEnd((Integer) row[5]);
				AssemblyComponent ac = new AssemblyComponent();
				ac.setName((String) row[6]);
				cvgla.setVariantGenomicLocationAssociationObject(ac);
				cvgla.setVariantAssociationSubject(variant);
				cvgla.setPredictedVariantConsequences(new ArrayList<>());
				variant.getCuratedVariantGenomicLocations().add(cvgla);
			}

			if (pvcId == null) {
				continue;
			}

			// Find or create the predicted variant consequence
			PredictedVariantConsequence pvc = cvgla.getPredictedVariantConsequences().stream()
				.filter(p -> pvcId.equals(p.getId())).findFirst().orElse(null);
			if (pvc == null) {
				pvc = new PredictedVariantConsequence();
				pvc.setId(pvcId);
				pvc.setVepConsequences(new ArrayList<>());
				if (row[9] != null) {
					VocabularyTerm vepImpact = new VocabularyTerm();
					vepImpact.setName((String) row[9]);
					pvc.setVepImpact(vepImpact);
				}
				if (row[10] != null) {
					VocabularyTerm siftPrediction = new VocabularyTerm();
					siftPrediction.setName((String) row[10]);
					pvc.setSiftPrediction(siftPrediction);
				}
				pvc.setSiftScore((Float) row[11]);
				if (row[12] != null) {
					VocabularyTerm polyphenPrediction = new VocabularyTerm();
					polyphenPrediction.setName((String) row[12]);
					pvc.setPolyphenPrediction(polyphenPrediction);
				}
				pvc.setPolyphenScore((Float) row[13]);
				if (row[14] != null || row[15] != null || row[16] != null) {
					Transcript transcript = new Transcript();
					transcript.setName((String) row[14]);
					transcript.setPrimaryExternalId((String) row[15]);
					transcript.setCurie((String) row[16]);
					if (row[17] != null) {
						SOTerm transcriptType = new SOTerm();
						transcriptType.setName((String) row[17]);
						transcript.setTranscriptType(transcriptType);
					}
					if (row[19] != null) {
						Gene gene = new Gene();
						gene.setPrimaryExternalId((String) row[19]);
						if (row[21] != null) {
							GeneSymbolSlotAnnotation annotation = new GeneSymbolSlotAnnotation();
							annotation.setDisplayText((String) row[21]);
							annotation.setFormatText((String) row[22]);
							gene.setGeneSymbol(annotation);
						}
						TranscriptGeneAssociation tga = new TranscriptGeneAssociation();
						tga.setTranscriptGeneAssociationObject(gene);
						transcript.setTranscriptGeneAssociations(List.of(tga));
					}
					pvc.setVariantTranscript(transcript);
				}
				pvc.setGeneLevelConsequence(row[18] != null ? (Boolean) row[18] : false);
				cvgla.getPredictedVariantConsequences().add(pvc);
			}

			if (row[7] != null) {
				SOTerm consequence = new SOTerm();
				consequence.setName((String) row[7]);
				if (pvc.getVepConsequences().stream().noneMatch(c -> consequence.getName().equals(c.getName()))) {
					pvc.getVepConsequences().add(consequence);
				}
			}
		}

		// Intron/Exon location computation
		Map<Long, PredictedVariantConsequence> pvcById = new HashMap<>();
		for (Map<Long, Variant> variantMap : alleleVariantMap.values()) {
			for (Variant v : variantMap.values()) {
				for (CuratedVariantGenomicLocationAssociation loc : v.getCuratedVariantGenomicLocations()) {
					for (PredictedVariantConsequence pvc : loc.getPredictedVariantConsequences()) {
						if (pvc.getId() != null && pvc.getVariantTranscript() != null) {
							pvcById.put(pvc.getId(), pvc);
						}
					}
				}
			}
		}

		if (!pvcById.isEmpty()) {
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

			Query intronExonQuery = entityManager.createNativeQuery(intronExonQueryString);
			intronExonQuery.setParameter("pvcIds", pvcById.keySet());
			List<Object[]> intronExonResults = intronExonQuery.getResultList();

			for (Object[] row : intronExonResults) {
				Long pvcId = (Long) row[0];
				PredictedVariantConsequence pvc = pvcById.get(pvcId);
				if (pvc != null) {
					pvc.setExons((String) row[1]);
					pvc.setIntrons((String) row[2]);
				}
			}
		}

		// Phenotype existence
		String phenotypeQueryString = """
				SELECT DISTINCT phenotypeannotationsubject_id as allele_id
				FROM allelephenotypeannotation
				WHERE phenotypeannotationsubject_id IN :alleleIds
				UNION
				SELECT DISTINCT inferredallele_id as allele_id
				FROM agmphenotypeannotation
				WHERE inferredallele_id IN :alleleIds
				UNION
				SELECT DISTINCT assertedalleles_id as allele_id
				FROM agmphenotypeannotation_allele
				WHERE assertedalleles_id IN :alleleIds
				""";

		Query phenotypeQuery = entityManager.createNativeQuery(phenotypeQueryString);
		phenotypeQuery.setParameter("alleleIds", alleleIds);
		List<Object> phenotypeResults = phenotypeQuery.getResultList();

		Set<Long> allelesWithPhenotype = phenotypeResults.stream()
				.map(obj -> (Long) obj)
				.collect(Collectors.toSet());

		// Disease existence
		String diseaseQueryString = """
				SELECT DISTINCT diseaseannotationsubject_id as allele_id
				FROM allelediseaseannotation
				WHERE diseaseannotationsubject_id IN :alleleIds
				UNION
				SELECT DISTINCT inferredallele_id as allele_id
				FROM agmdiseaseannotation
				WHERE inferredallele_id IN :alleleIds
				UNION
				SELECT DISTINCT assertedalleles_id as allele_id
				FROM agmdiseaseannotation_allele
				WHERE assertedalleles_id IN :alleleIds
				""";

		Query diseaseQuery = entityManager.createNativeQuery(diseaseQueryString);
		diseaseQuery.setParameter("alleleIds", alleleIds);
		List<Object> diseaseResults = diseaseQuery.getResultList();

		Set<Long> allelesWithDisease = diseaseResults.stream()
				.map(obj -> (Long) obj)
				.collect(Collectors.toSet());

		// Build DTOs
		List<AlleleSummaryDTO> dtos = new ArrayList<>();
		for (Allele allele : alleles) {
			List<Variant> variants = new ArrayList<>(alleleVariantMap.getOrDefault(allele.getId(), new HashMap<>()).values());
			Boolean hasPhenotype = allelesWithPhenotype.contains(allele.getId());
			Boolean hasDisease = allelesWithDisease.contains(allele.getId());
			dtos.add(new AlleleSummaryDTO(allele, variants, hasPhenotype, hasDisease));
		}

		// Notes
		String notesQueryString = """
				SELECT ben.submittedobject_id as allele_id,
				n.freetext as note,
				vt.id as notetype_id,
				vt.name as notetype_name
				from note n
				join biologicalentity_note ben on ben.relatednotes_id = n.id
				join vocabularyterm vt on vt.id = n.notetype_id
				WHERE ben.submittedobject_id IN :alleleIds
				and vt.name IN ('mutation_description', 'transgene_content_summary', 'transgene_construction_summary');
				""";
		Query notesQueryExec = entityManager.createNativeQuery(notesQueryString);
		notesQueryExec.setParameter("alleleIds", alleleIds);
		List<Object[]> notesResults = notesQueryExec.getResultList();

		notesResults.forEach(objects -> {
			Note note = new Note();
			note.setFreeText((String) objects[1]);

			VocabularyTerm noteType = new VocabularyTerm();
			noteType.setId((Long) objects[2]);
			noteType.setName((String) objects[3]);
			note.setNoteType(noteType);

			Long alleleId = (Long) objects[0];
			Allele allele = alleleMap.get(alleleId);
			List<Note> noteList = allele.getRelatedNotes();
			if (noteList == null) {
				noteList = new ArrayList<>();
			}
			noteList.add(note);
			allele.setRelatedNotes(noteList);
		});

		return dtos;
	}
}
