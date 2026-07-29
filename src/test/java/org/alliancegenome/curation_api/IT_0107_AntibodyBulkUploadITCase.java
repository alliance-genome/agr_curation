package org.alliancegenome.curation_api;

import static org.hamcrest.Matchers.is;

import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("107 - Antibody bulk upload")
@Order(107)
@SuppressWarnings("checkstyle:TypeNameCheck")
public class IT_0107_AntibodyBulkUploadITCase extends BaseITCase {

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String reference = "AGRKB:000000001";
	private final String referenceXref = "PMID:25920550";
	private final String dataProvider = "WB";

	private final String antibodyBulkPostEndpoint = "/api/antibody/bulk/WB/antibodies";
	private final String antibodyGetEndpoint = "/api/antibody/";
	private final String antibodyTestFilePath = "src/test/resources/bulk/07_antibody/";

	@Test
	@Order(1)
	public void antibodyBulkUploadCheckFields() throws Exception {
		checkSuccessfulBulkLoad(antibodyBulkPostEndpoint, antibodyTestFilePath + "AF_01_all_fields.json");

		RestAssured.given().
			when().
			get(antibodyGetEndpoint + "WB:AntibodyTest0001").
			then().
			statusCode(200).
			body("entity.primaryExternalId", is("WB:AntibodyTest0001")).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.name", is("anti-WNT-4")).
			body("entity.clonality.name", is("monoclonal")).
			body("entity.heavyChainIsotype.name", is("IgG")).
			body("entity.lightChainIsotype.name", is("k")).
			body("entity.antigenTaxon.curie", is("NCBITaxon:6239")).
			body("entity.taxon.curie", is("NCBITaxon:9606")).
			body("entity.references[0].curie", is(reference)).
			body("entity.references[0].crossReferences[0].referencedCurie", is(referenceXref)).
			body("entity.originalReference.curie", is(reference)).
			body("entity.secondaryIdentifiers[0]", is("WB:AntibodyTest-legacy-0001")).
			body("entity.dataProvider.abbreviation", is(dataProvider)).
			body("entity.dataProviderCrossReference.referencedCurie", is("TEST:0001"));
	}

	@Test
	@Order(2)
	public void antibodyBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(antibodyBulkPostEndpoint, antibodyTestFilePath + "MR_01_no_name.json");
		checkFailedBulkLoad(antibodyBulkPostEndpoint, antibodyTestFilePath + "MR_02_no_clonality.json");
	}

	@Test
	@Order(3)
	public void antibodyBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(antibodyBulkPostEndpoint, antibodyTestFilePath + "IV_01_invalid_clonality.json");
	}

	@Test
	@Order(4)
	public void antibodyBulkUploadInvalidReferenceWarning() throws Exception {
		checkWarningBulkLoad(antibodyBulkPostEndpoint, antibodyTestFilePath + "IV_02_invalid_reference.json");
	}
}
