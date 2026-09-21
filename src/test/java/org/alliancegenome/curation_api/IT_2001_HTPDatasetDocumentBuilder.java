package org.alliancegenome.curation_api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.alliancegenome.curation_api.model.document.builders.HTPDatasetDocumentBuilder;
import org.alliancegenome.curation_api.model.document.es.HTPDatasetSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.ontology.MMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("2001 - HTPDatasetDocumentBuilder")
@Order(2001)
@SuppressWarnings("checkstyle:TypeNameCheck")
class IT_2001_HTPDatasetDocumentBuilder {

	// SCRUM-6314: an assay MMO term with no display synonym (e.g. MMO:0000869
	// "single nucleus RNA-seq assay") must still contribute to the Assay facet,
	// falling back to the term's own name.
	@Test
	public void testAssayFallsBackToTermNameWhenNoDisplaySynonym() {
		MMOTerm assay = getAssay("single nucleus RNA-seq assay",
				getSynonym("snRNA-seq assay", false),
				getSynonym("sNuc-seq assay", false));

		HTPDatasetSearchResultDocument doc = HTPDatasetDocumentBuilder
				.buildSearchResultDocument(getDatasetAnnotation(getSample(assay)));

		assertTrue(doc.getAssays().contains("single nucleus RNA-seq assay"),
				"assay term name should be used when there is no display synonym");
		assertFalse(doc.getAssays().contains("snRNA-seq assay"),
				"non-display synonyms should not be used as facet values");
	}

	// When a display synonym exists, it (not the term name) is the facet value,
	// preserving the existing behaviour for assays that have one.
	@Test
	public void testAssayUsesDisplaySynonymWhenPresent() {
		MMOTerm assay = getAssay("single cell RNA-seq assay",
				getSynonym("scRNA-seq assay", true),
				getSynonym("ignored non-display synonym", false));

		HTPDatasetSearchResultDocument doc = HTPDatasetDocumentBuilder
				.buildSearchResultDocument(getDatasetAnnotation(getSample(assay)));

		assertTrue(doc.getAssays().contains("scRNA-seq assay"),
				"display synonym should be used as the facet value");
		assertFalse(doc.getAssays().contains("single cell RNA-seq assay"),
				"term name should not be used when a display synonym exists");
		assertFalse(doc.getAssays().contains("ignored non-display synonym"),
				"non-display synonyms should never be used as facet values");
	}

	// A sample with no assay type must not NPE the whole document build.
	@Test
	public void testNullAssayDoesNotBreakDocumentBuild() {
		HTPExpressionDatasetSampleAnnotation sample = getSample(null);

		HTPDatasetSearchResultDocument doc = assertDoesNotThrow(() -> HTPDatasetDocumentBuilder
				.buildSearchResultDocument(getDatasetAnnotation(sample)));

		assertTrue(doc.getAssays().isEmpty(), "a null assay should contribute no facet value");
	}

	private static HTPExpressionDatasetAnnotation getDatasetAnnotation(HTPExpressionDatasetSampleAnnotation... samples) {
		ExternalDataBaseEntity dataset = new ExternalDataBaseEntity();
		dataset.setCurie("WB:1005");
		dataset.setHtpExpressionDatasetSampleAnnotation(Arrays.asList(samples));

		HTPExpressionDatasetAnnotation annotation = new HTPExpressionDatasetAnnotation();
		annotation.setHtpExpressionDataset(dataset);
		return annotation;
	}

	private static HTPExpressionDatasetSampleAnnotation getSample(MMOTerm assay) {
		NCBITaxonTerm taxon = new NCBITaxonTerm();
		taxon.setName("Caenorhabditis elegans");

		HTPExpressionDatasetSampleAnnotation sample = new HTPExpressionDatasetSampleAnnotation();
		sample.setTaxon(taxon);
		sample.setHtpExpressionSampleLocations(new ArrayList<>());
		sample.setExpressionAssayUsed(assay);
		return sample;
	}

	private static MMOTerm getAssay(String name, Synonym... synonyms) {
		MMOTerm assay = new MMOTerm();
		assay.setName(name);
		assay.setSynonyms(new ArrayList<>(Arrays.asList(synonyms)));
		return assay;
	}

	private static Synonym getSynonym(String name, boolean isDisplaySynonym) {
		Synonym synonym = new Synonym();
		synonym.setName(name);
		synonym.setIsDisplaySynonym(isDisplaySynonym);
		return synonym;
	}
}
