package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;

/**
 * SCRUM-6757: the HGVS export of a data provider's variants, on the WB variant IT_0610 loads for
 * WB:AlleleWithVar1 and IT_0611 gives its predicted consequences.
 */
@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("613 - Variant HGVS export")
@Order(613)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0613_VariantHgvsExportITCase extends BaseITCase {

	private static final String ENDPOINT = "/api/variant/WB/hgvs";
	private static final String ALLELE_RESULT = "results.find { it.alleleId == 'WB:AlleleWithVar1' }";
	private static final String TRANSCRIPT_RESULT = ALLELE_RESULT + ".transcriptConsequences[0]";

	@Test
	@Order(1)
	public void exportsTheAllelesVariantWithItsHgvs() {
		RestAssured.given().
			when().
			get(ENDPOINT + "?limit=10000").
			then().
			statusCode(200).
			body(ALLELE_RESULT + ".hgvsG", is("NC_003279.8:g.1A>T")).
			body(ALLELE_RESULT + ".hgvsC", is(List.of("WB:Y74C9A.2a.1:c.29M>N"))).
			body(ALLELE_RESULT + ".hgvsP", is(List.of("WB:CE49439:p.Met10Neo"))).
			// The VEP result for each transcript, as IT_0611's update (11_vep/UD_01_update.json) left it
			body(TRANSCRIPT_RESULT + ".transcript", is("WB:Y74C9A.2a.1")).
			body(TRANSCRIPT_RESULT + ".molecularConsequences", is(List.of("splice_donor_5th_base_variant"))).
			body(TRANSCRIPT_RESULT + ".impact", is("MODIFIER")).
			body(TRANSCRIPT_RESULT + ".hgvsC", is("WB:Y74C9A.2a.1:c.29M>N")).
			body(TRANSCRIPT_RESULT + ".hgvsP", is("WB:CE49439:p.Met10Neo")).
			body(TRANSCRIPT_RESULT + ".siftPrediction", is("deleterious_low_confidence")).
			body(TRANSCRIPT_RESULT + ".polyphenPrediction", is("possibly_damaging")).
			body(TRANSCRIPT_RESULT + ".aminoAcidReference", is("M")).
			body(TRANSCRIPT_RESULT + ".aminoAcidVariant", is("N")).
			body(TRANSCRIPT_RESULT + ".proteinStart", is(247)).
			body(TRANSCRIPT_RESULT + ".proteinEnd", is(250)).
			body(ALLELE_RESULT + ".transcriptConsequences", hasSize(1));
	}

	@Test
	@Order(2)
	public void countsThePairs() {
		RestAssured.given().
			when().
			get(ENDPOINT + "?page=0&limit=0").
			then().
			statusCode(200).
			body("totalResults", greaterThanOrEqualTo(1)).
			body("returnedRecords", is(0));
	}

	@Test
	@Order(3)
	public void pagesThroughThePairs() {
		RestAssured.given().
			when().
			get(ENDPOINT + "?page=0&limit=1").
			then().
			statusCode(200).
			body("results", hasSize(1)).
			body("returnedRecords", is(1));
	}

	@Test
	@Order(4)
	public void dataProviderWithoutVariantsExportsNothing() {
		RestAssured.given().
			when().
			get("/api/variant/NOT_A_PROVIDER/hgvs?page=0&limit=0").
			then().
			statusCode(200).
			body("totalResults", is(0));

		RestAssured.given().
			when().
			get("/api/variant/NOT_A_PROVIDER/hgvs").
			then().
			statusCode(200).
			body("returnedRecords", is(0));
	}
}
