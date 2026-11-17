package org.alliancegenome.curation_api.model.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.CurationView;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

class VariantJsonTest {

	private final ObjectMapper mapper = JsonMapper.builder()
		.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
		.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
		.build();

	private Variant variantWithOneLocation() {
		Variant variant = new Variant();
		variant.setPrimaryExternalId("WB:WBVar00000001");

		GenomeAssembly assembly = new GenomeAssembly();
		assembly.setPrimaryExternalId("WBcel235");
		AssemblyComponent chromosome = new AssemblyComponent();
		chromosome.setName("X");
		chromosome.setGenomeAssembly(assembly);

		VocabularyTerm relation = new VocabularyTerm();
		relation.setName("located_on");

		Reference evidence = new Reference();
		evidence.setCurie("AGRKB:101000000000001");

		SOTerm vepConsequence = new SOTerm();
		vepConsequence.setName("missense_variant");
		PredictedVariantConsequence pvc = new PredictedVariantConsequence();
		pvc.setHgvsCodingNomenclature("NM_000001.1:c.1A>T");
		pvc.setVepConsequences(List.of(vepConsequence));

		CuratedVariantGenomicLocationAssociation location = new CuratedVariantGenomicLocationAssociation();
		location.setVariantAssociationSubject(variant);
		location.setVariantGenomicLocationAssociationObject(chromosome);
		location.setRelation(relation);
		location.setHgvs("NC_003284.9:g.100A>T");
		location.setStart(100);
		location.setEnd(100);
		location.setEvidence(List.of(evidence));
		location.setPredictedVariantConsequences(List.of(pvc));
		pvc.setVariantGenomicLocation(location);

		variant.setCuratedVariantGenomicLocations(List.of(location));
		return variant;
	}

	@Test
	void variantViewCarriesLocationsWithEvidenceButNotPredictedConsequences() throws Exception {
		String json = mapper.writerWithView(CurationView.VariantView.class).writeValueAsString(variantWithOneLocation());

		assertTrue(json.contains("\"curatedVariantGenomicLocations\""), json);
		assertTrue(json.contains("\"hgvs\":\"NC_003284.9:g.100A>T\""), json);
		assertTrue(json.contains("\"name\":\"X\""), json);
		assertTrue(json.contains("\"primaryExternalId\":\"WBcel235\""), json);
		assertTrue(json.contains("\"curie\":\"AGRKB:101000000000001\""), json);
		assertFalse(json.contains("\"predictedVariantConsequences\""), json);
	}

	@Test
	void variantViewReadsBackForAnUpdate() throws Exception {
		String json = mapper.writerWithView(CurationView.VariantView.class).writeValueAsString(variantWithOneLocation());

		Variant read = mapper.readValue(json, Variant.class);
		assertEquals("WB:WBVar00000001", read.getPrimaryExternalId());
		assertEquals(1, read.getCuratedVariantGenomicLocations().size());
		assertEquals("NC_003284.9:g.100A>T", read.getCuratedVariantGenomicLocations().get(0).getHgvs());
	}

	@Test
	void detailViewStillCarriesPredictedConsequences() throws Exception {
		String json = mapper.writerWithView(CurationView.VariantDetailView.class).writeValueAsString(variantWithOneLocation());

		assertTrue(json.contains("\"curatedVariantGenomicLocations\""), json);
		assertTrue(json.contains("\"predictedVariantConsequences\""), json);
		assertTrue(json.contains("\"hgvsCodingNomenclature\":\"NM_000001.1:c.1A>T\""), json);
	}

	@Test
	void detailViewCarriesEveryGenomicLocationColumn() throws Exception {
		String json = mapper.writerWithView(CurationView.VariantDetailView.class).writeValueAsString(variantWithOneLocation());

		assertTrue(json.contains("\"relation\":{"), json);
		assertTrue(json.contains("\"name\":\"X\""), json);
		assertTrue(json.contains("\"primaryExternalId\":\"WBcel235\""), json);
		assertTrue(json.contains("\"hgvs\":\"NC_003284.9:g.100A>T\""), json);
		assertTrue(json.contains("\"start\":100"), json);
		assertTrue(json.contains("\"end\":100"), json);
		assertTrue(json.contains("\"curie\":\"AGRKB:101000000000001\""), json);

		Variant read = mapper.readValue(json, Variant.class);
		assertEquals(1, read.getCuratedVariantGenomicLocations().get(0).getEvidence().size());
	}

	@Test
	void variantSummaryDocumentIsUnchanged() throws Exception {
		String json = mapper.writerWithView(CurationView.VariantSummaryDocument.class).writeValueAsString(variantWithOneLocation());

		assertTrue(json.contains("\"curatedVariantGenomicLocations\""), json);
		assertTrue(json.contains("\"predictedVariantConsequences\""), json);
		assertFalse(json.contains("\"evidence\""), json);
	}
}
