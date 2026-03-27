package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.constants.ValidationConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.document.es.GeneSearchResultDocument;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.exceptions.KnownIssueValidationException;
import org.alliancegenome.curation_api.exceptions.ObjectValidationException;
import org.alliancegenome.curation_api.exceptions.ValidationException;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.model.ingest.dto.GeneDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.associations.AlleleGeneAssociationService;
import org.alliancegenome.curation_api.services.associations.ConstructGenomicEntityAssociationService;
import org.alliancegenome.curation_api.services.base.SubmittedObjectCrudService;
import org.alliancegenome.curation_api.services.helpers.GeneXrefHelper;
import org.alliancegenome.curation_api.services.ontology.NcbiTaxonTermService;
import org.alliancegenome.curation_api.services.orthology.GeneToGeneOrthologyService;
import org.alliancegenome.curation_api.services.validation.GeneValidator;
import org.alliancegenome.curation_api.services.validation.dto.GeneDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneService extends SubmittedObjectCrudService<Gene, GeneDTO, GeneDAO> implements BasePopularityInterface {

	@Inject GeneDAO geneDAO;
	@Inject NoteService noteService;
	@Inject GeneValidator geneValidator;
	@Inject GeneDTOValidator geneDtoValidator;
	@Inject DiseaseAnnotationService diseaseAnnotationService;
	@Inject PersonService personService;
	@Inject GeneToGeneOrthologyService orthologyService;
	@Inject AlleleGeneAssociationService alleleGeneAssociationService;
	@Inject ConstructGenomicEntityAssociationService constructGenomicEntityAssociationService;
	@Inject GeneInteractionService geneInteractionService;
	@Inject PhenotypeAnnotationService phenotypeAnnotationService;
	@Inject NcbiTaxonTermService ncbiTaxonTermService;
	@Inject GeneXrefHelper geneXrefHelper;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> update(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneUpdate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> create(Gene uiEntity) {
		Gene dbEntity = geneValidator.validateGeneCreate(uiEntity);
		return new ObjectResponse<Gene>(dbEntity);
	}

	@Override
	public ObjectResponse<Gene> upsert(GeneDTO dto) throws ValidationException {
		return upsert(dto, null);
	}

	@Override
	public ObjectResponse<Gene> upsert(GeneDTO dto, BackendBulkDataProvider dataProvider) throws ValidationException {
		return geneDtoValidator.validateGeneDTO(dto, dataProvider);
	}

	@Override
	@Transactional
	public ObjectResponse<Gene> deleteById(Long id) {
		deprecateOrDelete(id, true, "Gene DELETE API call", false);
		ObjectResponse<Gene> ret = new ObjectResponse<>();
		return ret;
	}

	@Override
	@Transactional
	public Gene deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		Gene gene = geneDAO.find(id);
		List<String> deprecationReasons = new ArrayList<>();
		if (gene != null) {
			if (forceDeprecate) {
				deprecationReasons.add("Deprecation instead of deletion rule applied");
			}
			if (geneDAO.hasReferencingDiseaseAnnotations(id)) {
				deprecationReasons.add("Gene is referenced by disease annotation(s)");
			}
			if (geneDAO.hasReferencingPhenotypeAnnotations(id)) {
				deprecationReasons.add("Gene is referenced by phenotype annotation(s)");
			}
			if (geneDAO.hasReferencingOrthologyPairs(id)) {
				deprecationReasons.add("Gene is referenced by orthology pair(s)");
			}
			if (geneDAO.hasReferencingParalogyPairs(id)) {
				deprecationReasons.add("Gene is referenced by paralogy pair(s)");
			}
			if (geneDAO.hasReferencingInteractions(id)) {
				deprecationReasons.add("Gene is referenced by interaction(s)");
			}
			if (geneDAO.hasReferencingGeneExpressionAnnotations(id)) {
				deprecationReasons.add("Gene is referenced by expression annotation(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getAlleleGeneAssociations())) {
				deprecationReasons.add("Gene has allele association(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getGeneOntologyAnnotations())) {
				deprecationReasons.add("Gene is referenced by gene ontology annotation(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getSequenceTargetingReagentGeneAssociations())) {
				deprecationReasons.add("Gene has sequence targeting reagent association(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getTranscriptGeneAssociations())) {
				deprecationReasons.add("Gene has transcript association(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getGeneGenomicLocationAssociations())) {
				deprecationReasons.add("Gene has genomic location association(s)");
			}
			if (CollectionUtils.isNotEmpty(gene.getConstructGenomicEntityAssociations())) {
				deprecationReasons.add("Gene has construct association(s)");
			}
			if (CollectionUtils.isNotEmpty(deprecationReasons)) {
				if (!gene.getObsolete()) {
					gene.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					gene.setDateUpdated(OffsetDateTime.now());
					gene.setObsolete(true);
					
					Note deprecationNote = noteService.createDeprecationNote(gene.getIdentifier(), requestSource, deprecationReasons);
					if (gene.getRelatedNotes() == null) {
						gene.setRelatedNotes(new ArrayList<>());
					}
					gene.getRelatedNotes().add(deprecationNote);
					
					return geneDAO.persist(gene);
				} else {
					return gene;
				}
			} else {
				geneDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find Gene with id: " + id;
			if (throwApiError) {
				ObjectResponse<Gene> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> ids = geneDAO.findIdsByParams(params);
		ids.removeIf(Objects::isNull);

		return ids;
	}

	@Transactional
	public void addBiogridXref(String entrezId, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(entrezId, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField("crossReferences.referencedCurie", "NCBI_Gene:" + entrezId);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException("crossReferences - referencedCurie: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:" + entrezId + ")");
		}
		
		allianceGene = geneXrefHelper.addBiogridCrossReference(allianceGene, "NCBI_Gene:" + entrezId);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(entrezId, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:biogrid/orcs)");
		}
	}

	@Transactional
	public void addGeoXref(String entrezId, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(entrezId, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField("crossReferences.referencedCurie", "NCBI_Gene:" + entrezId);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException("crossReferences - referencedCurie: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:" + entrezId + ")");
		}
		
		allianceGene = geneXrefHelper.addGeoCrossReference(allianceGene, "NCBI_Gene:" + entrezId);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(entrezId, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (NCBI_Gene:gene/other_expression)");
		}
	}

	@Transactional
	public void addExpressionAtlasXref(String identifier, BackendBulkDataProvider dataProvider) throws ValidationException {
		NCBITaxonTerm taxon = ncbiTaxonTermService.getByCurie(dataProvider.canonicalTaxonCurie).getEntity();
		if (taxon == null) {
			throw new ObjectValidationException(identifier, "dataProvider - canonical taxon: " + ValidationConstants.INVALID_MESSAGE + " (" + dataProvider.canonicalTaxonCurie + " not found)");
		}

		
		String searchField;
		String searchValue;
		String referencedCurie;
		String resourceDescriptorPrefix;
		switch (dataProvider) {
			case FB -> {
				searchField = "primaryExternalId";
				searchValue = "FB:" + identifier;
				referencedCurie = searchValue;
				resourceDescriptorPrefix = "FB";
			}
			case SGD -> {
				searchField = "geneSymbol.displayText";
				searchValue = identifier;
				referencedCurie = "SGD:" + identifier;
				resourceDescriptorPrefix = "SGD";
			}
			default -> {
				searchField = "crossReferences.referencedCurie";
				searchValue = "ENSEMBL:" + identifier;
				referencedCurie = searchValue;
				resourceDescriptorPrefix = "ENSEMBL";
			}
		}
		
		Gene allianceGene = null;
		SearchResponse<Gene> searchResponse = findByField(searchField, searchValue);
		if (searchResponse != null) {
			// Need to check that returned gene belongs to MOD corresponding to taxon
			for (Gene searchResult : searchResponse.getResults()) {
				String resultDataProviderCoreGenus = BackendBulkDataProvider.getCoreGenus(searchResult.getDataProvider().getAbbreviation());
				if (taxon.getName().startsWith(resultDataProviderCoreGenus + " ")) {
					allianceGene = searchResult;
					break;
				}
				if (StringUtils.equals(taxon.getCurie(), "NCBITaxon:9606") && StringUtils.equals(searchResult.getDataProvider().getAbbreviation(), "RGD")) {
					allianceGene = searchResult;
					break;
				}
			}
		}
		
		if (allianceGene == null) {
			throw new KnownIssueValidationException(searchField + ": " + ValidationConstants.INVALID_MESSAGE + " (" + searchValue + ")");
		}
		
		allianceGene = geneXrefHelper.addExpressionAtlasXref(allianceGene, resourceDescriptorPrefix, referencedCurie);
		
		if (allianceGene == null) {
			throw new ObjectValidationException(identifier, "resourceDescriptorPage: " + ValidationConstants.INVALID_MESSAGE + " (" + resourceDescriptorPrefix + ":expression_atlas)");
		}
	}

	@Override
	@Transactional
	public void updatePopularity(String curie, Double popularity) {
		SearchResponse<Gene> searchResponse = findByField("primaryExternalId", curie);
		if (searchResponse != null) {
			Gene gene = searchResponse.getSingleResult();
			gene.setPopularity(popularity);
		}
	}

	public List<Long> getAllIds() {
		return geneDAO.getAllIds();
	}

	public List<Gene> findByIds(List<Long> ids) {
		return geneDAO.findByIds(ids);
	}

	// --- Batch assembly for GeneSearchResultDocument ---

	private static final Set<String> BIOTYPE_LEVEL_0 = Set.of(
		"protein_coding_gene", "pseudogene", "ncRNA_gene", "other_gene"
	);

	private static final Set<String> BIOTYPE_LEVEL_1 = Set.of(
		"unclassified_ncRNA_gene", "lncRNA_gene", "piRNA_gene", "miRNA_gene",
		"snoRNA_gene", "tRNA_gene", "snRNA_gene", "rRNA_gene", "enzymatic_RNA_gene",
		"SRP_RNA_gene", "scRNA_gene", "RNase_P_RNA_gene", "telomerase_RNA_gene",
		"RNase_MRP_RNA_gene", "unclassified_gene", "heritable_phenotypic_marker",
		"gene_segment", "pseudogenic_gene_segment", "transposable_element_gene",
		"blocked_reading_frame"
	);

	private static final Set<String> BIOTYPE_LEVEL_2 = Set.of(
		"unclassified_lncRNA_gene", "lncRNA_gene", "antisense_lncRNA_gene",
		"sense_intronic_ncRNA_gene", "bidirectional_promoter_lncRNA",
		"sense_overlap_ncRNA_gene"
	);

	public List<GeneSearchResultDocument> buildSearchResultDocuments(List<Long> geneIds) {
		if (CollectionUtils.isEmpty(geneIds)) {
			return new ArrayList<>();
		}

		// Run all batch queries in parallel
		ExecutorService executor = Executors.newFixedThreadPool(16);
		CompletableFuture<List<Object[]>> baseInfoFuture = CompletableFuture.supplyAsync(() -> geneDAO.getBaseGeneInfo(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> soAncestorsFuture = CompletableFuture.supplyAsync(() -> geneDAO.getSoTermAncestors(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> synonymsFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneSynonyms(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> secondaryIdsFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneSecondaryIds(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> crossRefsFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneCrossReferences(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> chromosomesFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneChromosomes(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> allelesFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneAlleles(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> phenotypesFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGenePhenotypeStatements(geneIds), executor);
		CompletableFuture<List<Object[]>> directDiseaseFuture = CompletableFuture.supplyAsync(() -> geneDAO.getDirectGeneDiseases(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> strictOrthoFuture = CompletableFuture.supplyAsync(() -> geneDAO.getStrictOrthologySymbols(geneIds), executor);
		CompletableFuture<List<Object[]>> orthoDiseaseFuture = CompletableFuture.supplyAsync(() -> geneDAO.getOrthologDiseases(geneIds), executor);
		CompletableFuture<List<Object[]>> goFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneGoTerms(geneIds), executor);
		CompletableFuture<List<Object[]>> subcellularFuture = CompletableFuture.supplyAsync(() -> geneDAO.getExpressionSubcellularCC(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> anatomicalFuture = CompletableFuture.supplyAsync(() -> geneDAO.getExpressionAnatomical(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> anatomicalSlimFuture = CompletableFuture.supplyAsync(() -> geneDAO.getExpressionAnatomicalSlim(geneIds), executor);
		CompletableFuture<List<Object[]>> whereExpressedFuture = CompletableFuture.supplyAsync(() -> geneDAO.getWhereExpressedAndStages(geneIds), executor);
		CompletableFuture<List<Object[]>> descriptionFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneDescriptions(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> modelsFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneModels(geneIds), executor);
		CompletableFuture<Map<Long, Set<String>>> diseaseAgrSlimFuture = CompletableFuture.supplyAsync(() -> geneDAO.getGeneDiseaseAgrSlim(geneIds), executor);

		CompletableFuture.allOf(baseInfoFuture, soAncestorsFuture, synonymsFuture, secondaryIdsFuture,
			crossRefsFuture, chromosomesFuture, allelesFuture, phenotypesFuture, directDiseaseFuture,
			strictOrthoFuture, orthoDiseaseFuture, goFuture, subcellularFuture, anatomicalFuture,
			anatomicalSlimFuture, whereExpressedFuture, descriptionFuture, modelsFuture, diseaseAgrSlimFuture).join();
		executor.shutdown();

		List<Object[]> baseInfoRows = baseInfoFuture.join();
		Map<Long, Set<String>> soAncestors = soAncestorsFuture.join();
		Map<Long, Set<String>> synonyms = synonymsFuture.join();
		Map<Long, Set<String>> secondaryIds = secondaryIdsFuture.join();
		Map<Long, Set<String>> crossRefs = crossRefsFuture.join();
		Map<Long, Set<String>> chromosomes = chromosomesFuture.join();
		Map<Long, Set<String>> alleles = allelesFuture.join();
		Map<Long, Set<String>> phenotypes = phenotypesFuture.join();
		List<Object[]> directDiseaseRows = directDiseaseFuture.join();
		Map<Long, Set<String>> strictOrthoSymbols = strictOrthoFuture.join();
		List<Object[]> orthoDiseaseRows = orthoDiseaseFuture.join();
		List<Object[]> goRows = goFuture.join();
		List<Object[]> subcellularRows = subcellularFuture.join();
		Map<Long, Set<String>> anatomical = anatomicalFuture.join();
		Map<Long, Set<String>> anatomicalSlim = anatomicalSlimFuture.join();
		List<Object[]> whereExpressedRows = whereExpressedFuture.join();
		List<Object[]> descriptionRows = descriptionFuture.join();
		Map<Long, Set<String>> models = modelsFuture.join();
		Map<Long, Set<String>> diseaseAgrSlim = diseaseAgrSlimFuture.join();

		// Build base documents from Q1
		Map<Long, GeneSearchResultDocument> docMap = new HashMap<>();
		Map<Long, String> speciesAbbrevMap = new HashMap<>();
		for (Object[] row : baseInfoRows) {
			Long id = (Long) row[0];
			GeneSearchResultDocument doc = new GeneSearchResultDocument();
			doc.setCurie((String) row[1]);

			String fullName = (String) row[2];
			String taxonName = (String) row[3];
			String speciesAbbrev = (String) row[4];
			String symbol = (String) row[5];
			String soTermCurie = (String) row[6];
			String soTermName = (String) row[7];
			if (speciesAbbrev != null) {
				speciesAbbrevMap.put(id, speciesAbbrev);
			}

			if (fullName != null) {
				doc.setName(fullName);
			}
			if (symbol != null && speciesAbbrev != null) {
				doc.setNameKey(symbol + " (" + speciesAbbrev + ")");
			} else if (symbol != null) {
				doc.setNameKey(symbol);
			}

			if (taxonName != null) {
				doc.setSpecies(taxonName);
			}
			doc.setSymbol(symbol);
			doc.setSoTermId(soTermCurie);
			doc.setSoTermName(soTermName);

			docMap.put(id, doc);
		}

		// Q2: Biotypes from SO term ancestors
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			if (doc.getSoTermName() != null) {
				Set<String> ancestors = soAncestors.getOrDefault(id, new HashSet<>());
				Set<String> biotypes = new HashSet<>(ancestors);
				biotypes.add(doc.getSoTermName());
				doc.setBiotypes(biotypes);
				doc.setSoTermNameWithParents(new HashSet<>(biotypes));
				handleBioTypes(doc);
				// Sync soTermNameWithParents with biotypes after handleBioTypes adds items
				doc.setSoTermNameWithParents(new HashSet<>(doc.getBiotypes()));
			}
		}

		// Q3-Q7: Simple set fields
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			Set<String> geneSynonyms = new HashSet<>(synonyms.getOrDefault(id, new HashSet<>()));
			if (doc.getName() != null) {
				geneSynonyms.add(doc.getName());
			}
			if (doc.getSymbol() != null) {
				geneSynonyms.add(doc.getSymbol());
			}
			doc.setSynonyms(geneSynonyms);
			doc.setSecondaryIds(secondaryIds.getOrDefault(id, new HashSet<>()));
			doc.setCrossReferences(crossRefs.getOrDefault(id, new HashSet<>()));
			doc.setChromosomes(chromosomes.getOrDefault(id, new HashSet<>()));
			Set<String> rawAlleles = alleles.getOrDefault(id, new HashSet<>());
			String abbrev = speciesAbbrevMap.get(id);
			if (abbrev != null && !rawAlleles.isEmpty()) {
				Set<String> formattedAlleles = new HashSet<>();
				for (String allele : rawAlleles) {
					formattedAlleles.add(allele + " (" + abbrev + ")");
				}
				doc.setAlleles(formattedAlleles);
			} else {
				doc.setAlleles(rawAlleles);
			}
		}

		// Q8: Phenotype statements
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			entry.getValue().setPhenotypeStatements(phenotypes.getOrDefault(entry.getKey(), new HashSet<>()));
		}

		// Q9 + Q10b: Diseases (direct gene + ortholog)
		Map<Long, Set<String>> diseases = new HashMap<>();
		Map<Long, Set<String>> diseasesWithParents = new HashMap<>();
		for (Object[] row : directDiseaseRows) {
			Long id = (Long) row[0];
			String diseaseName = (String) row[1];
			String ancestorName = (String) row[2];
			if (diseaseName != null) {
				diseases.computeIfAbsent(id, k -> new HashSet<>()).add(diseaseName);
				diseasesWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(diseaseName);
			}
			if (ancestorName != null) {
				diseasesWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(ancestorName);
			}
		}
		for (Object[] row : orthoDiseaseRows) {
			Long id = (Long) row[0];
			String diseaseName = (String) row[1];
			String ancestorName = (String) row[2];
			if (diseaseName != null) {
				diseases.computeIfAbsent(id, k -> new HashSet<>()).add(diseaseName);
				diseasesWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(diseaseName);
			}
			if (ancestorName != null) {
				diseasesWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(ancestorName);
			}
		}
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			doc.setDiseases(diseases.getOrDefault(id, new HashSet<>()));
			doc.setDiseasesWithParents(diseasesWithParents.getOrDefault(id, new HashSet<>()));
			doc.setDiseasesAgrSlim(diseaseAgrSlim.getOrDefault(id, new HashSet<>()));
		}

		// Q10a: Strict orthology symbols
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			entry.getValue().setStrictOrthologySymbols(strictOrthoSymbols.getOrDefault(entry.getKey(), new HashSet<>()));
		}

		// Q11: GO terms (namespace-based)
		Map<Long, Set<String>> ccWithParents = new HashMap<>();
		Map<Long, Set<String>> ccAgrSlim = new HashMap<>();
		Map<Long, Set<String>> bpWithParents = new HashMap<>();
		Map<Long, Set<String>> bpAgrSlim = new HashMap<>();
		Map<Long, Set<String>> mfWithParents = new HashMap<>();
		Map<Long, Set<String>> mfAgrSlim = new HashMap<>();
		for (Object[] row : goRows) {
			Long id = (Long) row[0];
			String namespace = (String) row[1];
			String termName = (String) row[2];
			boolean isAgrSlim = row[3] != null && (Boolean) row[3];
			if (namespace == null || termName == null) {
				continue;
			}
			switch (namespace) {
				case "cellular_component" -> {
					ccWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					if (isAgrSlim) {
						ccAgrSlim.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					}
				}
				case "biological_process" -> {
					bpWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					if (isAgrSlim) {
						bpAgrSlim.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					}
				}
				case "molecular_function" -> {
					mfWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					if (isAgrSlim) {
						mfAgrSlim.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
					}
				}
				default -> { }
			}
		}
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			doc.setCellularComponentWithParents(ccWithParents.getOrDefault(id, new HashSet<>()));
			doc.setCellularComponentAgrSlim(ccAgrSlim.getOrDefault(id, new HashSet<>()));
			doc.setBiologicalProcessWithParents(bpWithParents.getOrDefault(id, new HashSet<>()));
			doc.setBiologicalProcessAgrSlim(bpAgrSlim.getOrDefault(id, new HashSet<>()));
			doc.setMolecularFunctionWithParents(mfWithParents.getOrDefault(id, new HashSet<>()));
			doc.setMolecularFunctionAgrSlim(mfAgrSlim.getOrDefault(id, new HashSet<>()));
		}

		// Q12: Expression subcellular CC
		Map<Long, Set<String>> subCCWithParents = new HashMap<>();
		Map<Long, Set<String>> subCCAgrSlim = new HashMap<>();
		for (Object[] row : subcellularRows) {
			Long id = (Long) row[0];
			String termName = (String) row[1];
			boolean isAgrSlim = row[2] != null && (Boolean) row[2];
			if (termName != null) {
				subCCWithParents.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
				if (isAgrSlim) {
					subCCAgrSlim.computeIfAbsent(id, k -> new HashSet<>()).add(termName);
				}
			}
		}
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			doc.setSubcellularExpressionWithParents(subCCWithParents.getOrDefault(id, new HashSet<>()));
			doc.setSubcellularExpressionAgrSlim(subCCAgrSlim.getOrDefault(id, new HashSet<>()));
		}

		// Q13: Expression anatomical
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			entry.getValue().setAnatomicalExpressionSlim(anatomicalSlim.getOrDefault(entry.getKey(), new HashSet<>()));
			entry.getValue().setAnatomicalExpressionWithParents(anatomical.getOrDefault(entry.getKey(), new HashSet<>()));
		}

		// Q14: Where expressed + expression stages
		Map<Long, Set<String>> whereExpressed = new HashMap<>();
		Map<Long, Set<String>> expressionStages = new HashMap<>();
		for (Object[] row : whereExpressedRows) {
			Long id = (Long) row[0];
			String whereExpr = (String) row[1];
			String stage = (String) row[2];
			if (whereExpr != null) {
				whereExpressed.computeIfAbsent(id, k -> new HashSet<>()).add(whereExpr);
			}
			if (stage != null) {
				expressionStages.computeIfAbsent(id, k -> new HashSet<>()).add(stage);
			}
		}
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			GeneSearchResultDocument doc = entry.getValue();
			doc.setWhereExpressed(whereExpressed.getOrDefault(id, new HashSet<>()));
			doc.setExpressionStages(expressionStages.getOrDefault(id, new HashSet<>()));
		}

		// Q15: Gene descriptions
		for (Object[] row : descriptionRows) {
			Long id = (Long) row[0];
			String noteType = (String) row[1];
			String freetext = (String) row[2];
			GeneSearchResultDocument doc = docMap.get(id);
			if (doc != null && freetext != null) {
				if ("automated_gene_description".equals(noteType)) {
					doc.setAutomatedGeneDescription(freetext);
				} else if ("MOD_provided_gene_description".equals(noteType)) {
					doc.setGeneDescription(freetext);
				}
			}
		}

		// Q16: Models (Gene -> Allele -> AGM)
		for (Map.Entry<Long, GeneSearchResultDocument> entry : docMap.entrySet()) {
			Long id = entry.getKey();
			Set<String> rawModels = models.getOrDefault(id, new HashSet<>());
			String abbrev = speciesAbbrevMap.get(id);
			if (abbrev != null && !rawModels.isEmpty()) {
				Set<String> formattedModels = new HashSet<>();
				for (String model : rawModels) {
					formattedModels.add(model + " (" + abbrev + ")");
				}
				entry.getValue().setModels(formattedModels);
			} else {
				entry.getValue().setModels(rawModels);
			}
		}

		// Return documents in input order
		List<GeneSearchResultDocument> result = new ArrayList<>();
		for (Long id : geneIds) {
			GeneSearchResultDocument doc = docMap.get(id);
			if (doc != null) {
				result.add(doc);
			}
		}
		return result;
	}

	private static void handleBioTypes(GeneSearchResultDocument doc) {
		Set<String> allBiotypes = doc.getBiotypes();

		doc.setBiotype0(new HashSet<String>());
		doc.getBiotype0().add(doc.getSoTermName());
		doc.setBiotype0(new HashSet<>(CollectionUtils.intersection(allBiotypes, BIOTYPE_LEVEL_0)));
		doc.setBiotype1(new HashSet<>(CollectionUtils.intersection(allBiotypes, BIOTYPE_LEVEL_1)));
		doc.setBiotype2(new HashSet<>(CollectionUtils.intersection(allBiotypes, BIOTYPE_LEVEL_2)));

		if (doc.getBiotypes().contains("ncRNA_gene") && CollectionUtils.isEmpty(doc.getBiotype1())) {
			doc.getBiotypes().add("unclassified ncRNA gene");
			doc.getBiotype1().add("unclassified ncRNA gene");
		}

		if (doc.getBiotypes().contains("lncRNA_gene") && CollectionUtils.isEmpty(doc.getBiotype2())) {
			doc.getBiotypes().add("unclassified lncRNA gene");
			doc.getBiotype2().add("unclassified lncRNA gene");
		}

		if (CollectionUtils.isEmpty(doc.getBiotype0())) {
			doc.getBiotypes().add("other_gene");
			doc.getBiotype0().add("other_gene");

			if ("gene".equals(doc.getSoTermName())) {
				doc.getBiotype1().add("unclassified gene");
			}
			if ("biological_region".equals(doc.getSoTermName())) {
				doc.getBiotype1().add("unclassified biological region");
			}

			doc.getBiotypes().addAll(doc.getBiotype1());
		}
	}
}
