package org.alliancegenome.curation_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.SpeciesDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpeciesServiceTest {

	private static final List<Species> LOADS = List.of(
		species("FB", "FB", "NCBITaxon:7227"),
		species("HUMAN", "RGD", "NCBITaxon:9606"),
		species("MGI", "MGI", "NCBITaxon:10090"),
		species("RGD", "RGD", "NCBITaxon:10116"),
		species("SGD", "SGD", "NCBITaxon:559292"),
		species("WB", "WB", "NCBITaxon:6239"),
		species("XBXL", "XB", "NCBITaxon:8355"),
		species("XBXT", "XB", "NCBITaxon:8364"),
		species("ZFIN", "ZFIN", "NCBITaxon:7955"));

	private static final Set<String> MULTIPLE_SPECIES_LOADS = Set.of("HUMAN", "RGD", "XBXL", "XBXT");

	private SpeciesDAO speciesDAO;
	private SpeciesService speciesService;

	@BeforeEach
	void setUp() {
		speciesDAO = mock(SpeciesDAO.class);
		Map<String, List<Species>> loadsPerProvider = LOADS.stream()
			.collect(Collectors.groupingBy(s -> s.getDataProvider().getAbbreviation()));
		loadsPerProvider.forEach((abbreviation, species) -> {
			SearchResponse<Species> response = new SearchResponse<>();
			response.setResults(species);
			when(speciesDAO.findByField(EntityFieldConstants.DATA_PROVIDER, abbreviation)).thenReturn(response);
		});
		LOADS.forEach(species -> {
			SearchResponse<Species> response = new SearchResponse<>();
			response.setResults(List.of(species));
			when(speciesDAO.findByField("displayName", species.getDisplayName())).thenReturn(response);
		});
		speciesService = new SpeciesService();
		speciesService.speciesDAO = speciesDAO;
	}

	@Test
	void everyLoadCodeGetsTheExpectedTaxonFilter() {
		assertEquals(9, LOADS.size());
		for (Species species : LOADS) {
			boolean shared = MULTIPLE_SPECIES_LOADS.contains(species.getDisplayName());
			assertEquals(shared, speciesService.hasMultipleSpecies(species), species.getDisplayName());
			assertEquals(shared ? species.getTaxon().getCurie() : null, speciesService.getTaxonFilter(species), species.getDisplayName());
		}
	}

	@Test
	void dataProviderLookupIsCachedPerProvider() {
		for (int i = 0; i < 2; i++) {
			LOADS.forEach(speciesService::hasMultipleSpecies);
		}
		LOADS.stream().map(s -> s.getDataProvider().getAbbreviation()).distinct()
			.forEach(abbreviation -> verify(speciesDAO, times(1)).findByField(EntityFieldConstants.DATA_PROVIDER, abbreviation));
	}

	@Test
	void unknownDataProviderHasSingleSpecies() {
		Species species = species("NEW", "NEW", "NCBITaxon:1");
		assertFalse(speciesService.hasMultipleSpecies(species));
		assertNull(speciesService.getTaxonFilter(species));
	}

	@Test
	void everyLoadCodeResolvesToItsSpecies() {
		for (Species species : LOADS) {
			assertSame(species, speciesService.getByDisplayName(species.getDisplayName()));
		}
	}

	@Test
	void unknownLoadCodeIsRejected() {
		assertThrows(ApiErrorException.class, () -> speciesService.getByDisplayName("XB"));
		assertThrows(ApiErrorException.class, () -> speciesService.getByDisplayName(null));
	}

	private static Species species(String displayName, String dataProviderAbbreviation, String taxonCurie) {
		Organization dataProvider = new Organization();
		dataProvider.setAbbreviation(dataProviderAbbreviation);
		NCBITaxonTerm taxon = new NCBITaxonTerm();
		taxon.setCurie(taxonCurie);
		Species species = new Species();
		species.setDisplayName(displayName);
		species.setDataProvider(dataProvider);
		species.setTaxon(taxon);
		return species;
	}
}
