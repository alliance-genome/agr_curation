package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
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

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("0312 - CrossReferenceOrphanITCase")
@Order(312)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0312_CrossReferenceOrphanITCase extends BaseITCase {

	private static final String REFERENCE_CURIE = "AGRKB:scrum6053-orphan-1";
	private static final String XREF_KEPT = "PMID:6053TEST-KEPT";
	private static final String XREF_DROPPED = "PMID:6053TEST-DROPPED";

	private static final String DOTERM_CURIE = "DOID:scrum6053-orphan-2";
	private static final String DOTERM_XREF_KEPT = "MESH:6053TEST-ONT-KEPT";
	private static final String DOTERM_XREF_DROPPED = "MESH:6053TEST-ONT-DROPPED";

	@Test
	@Order(1)
	public void perEntityIngestRemovesOrphan() {
		Reference reference = new Reference();
		reference.setCurie(REFERENCE_CURIE);
		reference.setObsolete(false);
		reference.setInternal(false);
		reference.setCrossReferences(Set.of(buildXref(XREF_KEPT), buildXref(XREF_DROPPED)));

		RestAssured.given().
			contentType("application/json").
			body(reference).
			when().
			post("/api/reference").
			then().
			statusCode(200);

		io.restassured.path.json.JsonPath initial = RestAssured.given().
			when().
			get("/api/reference/" + REFERENCE_CURIE).
			then().
			statusCode(200).
			body("entity.crossReferences", hasSize(2)).
			extract().
			jsonPath();

		Long droppedXrefId = initial.getLong("entity.crossReferences.find { it.referencedCurie == '" + XREF_DROPPED + "' }.id");

		Reference fetched = initial.getObject("entity", Reference.class);
		fetched.setCrossReferences(fetched.getCrossReferences().stream()
			.filter(xref -> XREF_KEPT.equals(xref.getReferencedCurie()))
			.collect(Collectors.toSet()));

		RestAssured.given().
			contentType("application/json").
			body(fetched).
			when().
			put("/api/reference").
			then().
			statusCode(200);

		RestAssured.given().
			when().
			get("/api/reference/" + REFERENCE_CURIE).
			then().
			statusCode(200).
			body("entity.crossReferences", hasSize(1)).
			body("entity.crossReferences[0].referencedCurie", is(XREF_KEPT)).
			body("entity.crossReferences.referencedCurie", not(hasItem(XREF_DROPPED)));

		RestAssured.given().
			when().
			get("/api/cross-reference/" + droppedXrefId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	@Test
	@Order(2)
	public void ontologyTermPerEntityIngestRemovesOrphan() {
		DOTerm doTerm = new DOTerm();
		doTerm.setCurie(DOTERM_CURIE);
		doTerm.setName("SCRUM-6053 orphan test term");
		doTerm.setNamespace("disease_ontology");
		doTerm.setObsolete(false);
		doTerm.setInternal(false);
		List<CrossReference> xrefs = new ArrayList<>();
		xrefs.add(buildXref(DOTERM_XREF_KEPT));
		xrefs.add(buildXref(DOTERM_XREF_DROPPED));
		doTerm.setCrossReferences(xrefs);

		RestAssured.given().
			contentType("application/json").
			body(doTerm).
			when().
			post("/api/doterm").
			then().
			statusCode(200);

		// /find returns FieldsAndLists view, which includes crossReferences
		// (the default GET endpoint only serializes FieldsOnly).
		io.restassured.path.json.JsonPath initial = RestAssured.given().
			contentType("application/json").
			body("{\"curie\":\"" + DOTERM_CURIE + "\"}").
			when().
			post("/api/doterm/find?limit=1&page=0").
			then().
			statusCode(200).
			body("results", hasSize(1)).
			body("results[0].crossReferences", hasSize(2)).
			extract().
			jsonPath();

		Long droppedXrefId = initial.getLong("results[0].crossReferences.find { it.referencedCurie == '" + DOTERM_XREF_DROPPED + "' }.id");

		DOTerm fetched = initial.getObject("results[0]", DOTerm.class);
		fetched.setCrossReferences(fetched.getCrossReferences().stream()
			.filter(xref -> DOTERM_XREF_KEPT.equals(xref.getReferencedCurie()))
			.collect(Collectors.toList()));

		RestAssured.given().
			contentType("application/json").
			body(fetched).
			when().
			put("/api/doterm").
			then().
			statusCode(200);

		RestAssured.given().
			contentType("application/json").
			body("{\"curie\":\"" + DOTERM_CURIE + "\"}").
			when().
			post("/api/doterm/find?limit=1&page=0").
			then().
			statusCode(200).
			body("results[0].crossReferences", hasSize(1)).
			body("results[0].crossReferences[0].referencedCurie", is(DOTERM_XREF_KEPT)).
			body("results[0].crossReferences.referencedCurie", not(hasItem(DOTERM_XREF_DROPPED)));

		RestAssured.given().
			when().
			get("/api/cross-reference/" + droppedXrefId).
			then().
			statusCode(200).
			body("entity", nullValue());
	}

	private CrossReference buildXref(String curie) {
		CrossReference xref = new CrossReference();
		xref.setReferencedCurie(curie);
		xref.setDisplayName(curie);
		return xref;
	}
}
