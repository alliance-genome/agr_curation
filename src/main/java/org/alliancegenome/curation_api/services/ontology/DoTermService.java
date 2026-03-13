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

	public List<Long> getAllIds() {
		return doTermDAO.getAllIds();
	}

	public List<DOTerm> findByIds(List<Long> ids) {
		return doTermDAO.findByIds(ids);
	}

	public List<DiseaseSearchResultDocument> buildSearchResultDocuments(List<Long> ids) {
		List<Object[]> baseFields = doTermDAO.findBaseFieldsByIds(ids);
		List<Object[]> synonymRows = doTermDAO.findSynonymsByIds(ids);
		List<Object[]> crossRefRows = doTermDAO.findCrossReferencesByIds(ids);
		List<Object[]> secondaryIdRows = doTermDAO.findSecondaryIdsByIds(ids);
		List<Object[]> geneRows = doTermDAO.findGenesAndSpeciesByIds(ids);
		List<Object[]> diseaseGroupRows = doTermDAO.findDiseaseGroupByIds(ids);

		Map<Long, Set<String>> synonymsMap = groupToSet(synonymRows);
		Map<Long, Set<String>> crossRefsMap = groupToSet(crossRefRows);
		Map<Long, Set<String>> secondaryIdsMap = groupToSet(secondaryIdRows);
		Map<Long, Set<String>> diseaseGroupMap = groupToSet(diseaseGroupRows);

		// Gene rows: (doterm_id, gene_symbol, abbreviation, genus_species)
		Map<Long, Set<String>> genesMap = new HashMap<>();
		Map<Long, Set<String>> speciesMap = new HashMap<>();
		for (Object[] row : geneRows) {
			Long dotermId = (Long) row[0];
			String symbol = (String) row[1];
			String abbreviation = (String) row[2];
			String genusSpecies = (String) row[3];

			genesMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(symbol + " (" + abbreviation + ")");
			speciesMap.computeIfAbsent(dotermId, k -> new HashSet<>()).add(genusSpecies);
		}

		List<DiseaseSearchResultDocument> docs = new ArrayList<>();
		for (Object[] base : baseFields) {
			Long id = (Long) base[0];
			String curie = (String) base[1];
			String name = (String) base[2];
			String definition = (String) base[3];

			DiseaseSearchResultDocument doc = new DiseaseSearchResultDocument();
			doc.setCurie(curie);
			doc.setPrimaryKey(curie);
			doc.setSearchable(false);
			doc.setName(name);
			doc.setNameKey(name);
			doc.setDefinition(definition);
			doc.setSynonyms(synonymsMap.getOrDefault(id, new HashSet<>()));
			doc.setCrossReferences(crossRefsMap.getOrDefault(id, new HashSet<>()));
			doc.setSecondaryIds(secondaryIdsMap.getOrDefault(id, new HashSet<>()));
			doc.setGenes(genesMap.getOrDefault(id, new HashSet<>()));
			doc.setAssociatedSpecies(speciesMap.getOrDefault(id, new HashSet<>()));
			doc.setDiseaseGroup(diseaseGroupMap.getOrDefault(id, new HashSet<>()));
			docs.add(doc);
		}

		return docs;
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
