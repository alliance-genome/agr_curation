package org.alliancegenome.curation_api.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * SCRUM-6678: the DIOPT orthology/paralogy files give yeast genes the species-level taxon 4932,
 * while SGD itself is keyed to NCBITaxon:559292. Both must resolve to the SGD: prefix, otherwise
 * every yeast pair is skipped and then deleted by the load's cleanup.
 */
class BackendBulkDataProviderTest {

	@Test
	void sgdCanonicalTaxonResolvesToSgdPrefix() {
		assertEquals("SGD:", BackendBulkDataProvider.getCuriePrefixFromTaxonId(559292));
	}

	@Test
	void yeastSpeciesTaxonResolvesToSgdPrefix() {
		assertEquals("SGD:", BackendBulkDataProvider.getCuriePrefixFromTaxonId(4932));
	}

	@Test
	void otherProvidersStillResolve() {
		assertEquals("WB:", BackendBulkDataProvider.getCuriePrefixFromTaxonId(6239));
		assertEquals("HGNC:", BackendBulkDataProvider.getCuriePrefixFromTaxonId(9606));
	}

	@Test
	void unknownTaxonResolvesToNull() {
		assertNull(BackendBulkDataProvider.getCuriePrefixFromTaxonId(1));
	}
}
