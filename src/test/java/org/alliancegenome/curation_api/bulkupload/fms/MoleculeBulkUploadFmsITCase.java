package org.alliancegenome.curation_api.bulkupload.fms;

import java.io.IOException;
import java.nio.file.*;

import org.alliancegenome.curation_api.resources.TestElasticSearchResource;
import org.junit.jupiter.api.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.hasItem;

@QuarkusIntegrationTest
@QuarkusTestResource(TestElasticSearchResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("05 - Molecule bulk upload - FMS")
@Order(5)
public class MoleculeBulkUploadFmsITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 100000)
					.setParam("http.connection.timeout", 100000));
	}

	@Test
	@Order(1)
	public void moleculeBulkUploadCheckFields() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/01_all_fields.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(1)).
				body("results", hasSize(1)).
				body("results[0].curie", is("TEST:TestMol00000001")).
				body("results[0].name", is("Test molecule 1")).
				body("results[0].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
				body("results[0].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
				body("results[0].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
				body("results[0].formula", is("C15H20O4")).
				body("results[0].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
				body("results[0].synonyms[0]", is("TM1")).
				body("results[0].synonyms[1]", is("TestMol1")).
				body("results[0].synonyms", hasSize(2)).
				body("results[0].internal", is(false)).
				body("results[0].crossReferences[0].curie", is("TEST:TestMol00000001")).
				body("results[0].crossReferences[0].pageAreas[0]", is("molecule")).
				body("results[0].crossReferences", hasSize(1));
	}

	@Test
	@Order(2)
	public void moleculeBulkUploadNoId() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/02_no_id.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);
		
		RestAssured.given().
				when(). 
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(1));
	}

	@Test
	@Order(3)
	public void moleculeBulkUploadNoName() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/03_no_name.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);
		
		RestAssured.given().
				when(). 
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(1));
	}
	
	@Test
	@Order(4)
	public void moleculeBulkUploadNoInchi() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/04_no_inchi.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(2));
	}
	
	@Test
	@Order(5)
	public void moleculeBulkUploadNoInchiKey() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/05_no_inchikey.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(3));
	}
	
	@Test
	@Order(6)
	public void moleculeBulkUploadNoIupac() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/06_no_iupac.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(4));
	}
	
	@Test
	@Order(7)
	public void moleculeBulkUploadNoFormula() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/07_no_formula.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(5));
	}
	
	@Test
	@Order(8)
	public void moleculeBulkUploadNoSmiles() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/08_no_smiles.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(6));
	}
	
	@Test
	@Order(9)
	public void moleculeBulkUploadNoSynonyms() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/09_no_synonyms.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(7));
	}
	
	@Test
	@Order(10)
	public void moleculeBulkUploadNoCrossReferences() throws Exception {
		String content = Files.readString(Path.of("src/test/resources/bulk/fms/05_molecule/10_no_cross_references.json"));

		// upload file
		RestAssured.given().
				contentType("application/json").
				body(content).
				when().
				post("/api/molecule/bulk/moleculefile").
				then().
				statusCode(200);

		// check if all the fields are correctly read
		RestAssured.given().
				when().
				header("Content-Type", "application/json").
				body("{}").
				post("/api/molecule/find?limit=20&page=0").
				then().
				statusCode(200).
				body("totalResults", is(8));
	}
}
