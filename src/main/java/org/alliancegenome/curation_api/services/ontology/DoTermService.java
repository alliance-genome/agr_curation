package org.alliancegenome.curation_api.services.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alliancegenome.curation_api.dao.ontology.DoTermDAO;
import org.alliancegenome.curation_api.interfaces.base.BasePopularityInterface;
import org.alliancegenome.curation_api.model.document.es.DiseaseSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.services.base.BaseOntologyTermService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestScoped
public class DoTermService extends BaseOntologyTermService<DOTerm, DoTermDAO> implements BasePopularityInterface {

	@Inject
	DoTermDAO doTermDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(doTermDAO);
	}

	@Transactional
	public void updatePopularity(String curie, Double popularity) {
		DOTerm term = findByCurie(curie);
		if (term != null) {
			term.setPopularity(popularity);
		}
	}

	public List<DiseaseSearchResultDocument> buildAllSearchResultDocuments() {
		log.info("Building all disease search result documents...");

		log.info("Loading gene symbol cache...");
		Map<Long, String[]> geneSymbolCache = buildGeneSymbolCache();
		log.info("Cached {} gene symbols", geneSymbolCache.size());

		log.info("Loading base fields...");
		List<Object[]> baseFields = doTermDAO.findAllBaseFields();
		log.info("Loaded {} DOTerms", baseFields.size());

		log.info("Loading synonyms...");
		Map<Long, Set<String>> synonymsMap = groupToSet(doTermDAO.findAllSynonyms());

		log.info("Loading cross references...");
		Map<Long, Set<String>> crossRefsMap = groupToSet(doTermDAO.findAllCrossReferences());

		log.info("Loading secondary IDs...");
		Map<Long, Set<String>> secondaryIdsMap = groupToSet(doTermDAO.findAllSecondaryIds());

		log.info("Loading disease groups...");
		Map<Long, Set<String>> diseaseGroupMap = groupToSet(doTermDAO.findAllDiseaseGroups());

		log.info("Loading allele symbol cache...");
		Map<Long, String[]> alleleSymbolCache = buildAlleleSymbolCache();
		log.info("Cached {} allele symbols", alleleSymbolCache.size());

		log.info("Loading AGM name cache...");
		Map<Long, String[]> agmNameCache = buildAgmNameCache();
		log.info("Cached {} AGM names", agmNameCache.size());

		log.info("Loading disease-gene mappings...");
		List<Object[]> geneIdRows = doTermDAO.findDiseaseGeneIds();
		log.info("Loaded {} disease-gene pairs", geneIdRows.size());

		Map<Long, Set<String>> genesMap = new HashMap<>();
		Map<Long, Set<String>> speciesMap = new HashMap<>();
		for (Object[] row : geneIdRows) {
			Long dotermId = (Long) row[0];
			Long geneId = (Long) row[1];
			String[] geneInfo = geneSymbolCache.get(geneId);
			if (geneInfo == null) {
				continue;
			}
			String symbol = geneInfo[0];
			String abbreviation = geneInfo[1];
			String genusSpecies = geneInfo[2];
			genesMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(symbol + " (" + abbreviation + ")");
			speciesMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(genusSpecies);
		}

		log.info("Loading disease-allele mappings...");
		List<Object[]> alleleIdRows = doTermDAO.findDiseaseAlleleIds();
		log.info("Loaded {} disease-allele pairs", alleleIdRows.size());

		Map<Long, Set<String>> allelesMap = new HashMap<>();
		for (Object[] row : alleleIdRows) {
			Long dotermId = (Long) row[0];
			Long alleleId = (Long) row[1];
			String[] alleleInfo = alleleSymbolCache.get(alleleId);
			if (alleleInfo == null) {
				continue;
			}
			String symbol = alleleInfo[0];
			String abbreviation = alleleInfo[1];
			allelesMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(symbol + " (" + abbreviation + ")");
		}

		log.info("Loading disease-model mappings...");
		List<Object[]> agmIdRows = doTermDAO.findDiseaseAgmIds();
		log.info("Loaded {} disease-model pairs", agmIdRows.size());

		Map<Long, Set<String>> modelsMap = new HashMap<>();
		for (Object[] row : agmIdRows) {
			Long dotermId = (Long) row[0];
			Long agmId = (Long) row[1];
			String[] agmInfo = agmNameCache.get(agmId);
			if (agmInfo == null) {
				continue;
			}
			String name = agmInfo[0];
			String abbreviation = agmInfo[1];
			modelsMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(name + " (" + abbreviation + ")");
		}

		log.info("Assembling documents...");
		List<DiseaseSearchResultDocument> docs = new ArrayList<>();
		for (Object[] base : baseFields) {
			Long id = (Long) base[0];
			String curie = (String) base[1];
			String name = (String) base[2];
			String definition = (String) base[3];

			DiseaseSearchResultDocument doc = new DiseaseSearchResultDocument();
			doc.setCurie(curie);
			doc.setPrimaryKey(curie);
			doc.setName(name);
			doc.setNameKey(name);
			doc.setDefinition(definition);
			doc.setSynonyms(synonymsMap.getOrDefault(id, new HashSet<>()));
			doc.setCrossReferences(crossRefsMap.getOrDefault(id, new HashSet<>()));
			doc.setSecondaryIds(secondaryIdsMap.getOrDefault(id, new HashSet<>()));
			doc.setGenes(genesMap.getOrDefault(id, new HashSet<>()));
			doc.setAlleles(allelesMap.getOrDefault(id, new HashSet<>()));
			doc.setModels(modelsMap.getOrDefault(id, new HashSet<>()));
			doc.setAssociatedSpecies(speciesMap.getOrDefault(id, new HashSet<>()));
			doc.setDiseaseGroup(diseaseGroupMap.getOrDefault(id, new HashSet<>()));
			docs.add(doc);
		}

		log.info("Built {} disease search result documents", docs.size());
		return docs;
	}

	private Map<Long, String[]> buildGeneSymbolCache() {
		List<Object[]> rows = doTermDAO.findAllGeneSymbols();
		Map<Long, String[]> cache = new HashMap<>();
		for (Object[] row : rows) {
			Long geneId = (Long) row[0];
			String symbol = (String) row[1];
			String abbreviation = (String) row[2];
			String genusSpecies = (String) row[3];
			cache.put(geneId, new String[]{symbol, abbreviation, genusSpecies});
		}
		return cache;
	}

	private Map<Long, String[]> buildAlleleSymbolCache() {
		List<Object[]> rows = doTermDAO.findAllAlleleSymbols();
		Map<Long, String[]> cache = new HashMap<>();
		for (Object[] row : rows) {
			Long alleleId = (Long) row[0];
			String symbol = (String) row[1];
			String abbreviation = (String) row[2];
			String genusSpecies = (String) row[3];
			cache.put(alleleId, new String[]{symbol, abbreviation, genusSpecies});
		}
		return cache;
	}

	private Map<Long, String[]> buildAgmNameCache() {
		List<Object[]> rows = doTermDAO.findAllAgmNames();
		Map<Long, String[]> cache = new HashMap<>();
		for (Object[] row : rows) {
			Long agmId = (Long) row[0];
			String name = (String) row[1];
			String abbreviation = (String) row[2];
			String genusSpecies = (String) row[3];
			cache.put(agmId, new String[]{name, abbreviation, genusSpecies});
		}
		return cache;
	}

	private Map<Long, Set<String>> groupToSet(List<Object[]> rows) {
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
