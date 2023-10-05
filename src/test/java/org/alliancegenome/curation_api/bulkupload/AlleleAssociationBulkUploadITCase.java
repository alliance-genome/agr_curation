package org.alliancegenome.curation_api.bulkupload;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.time.OffsetDateTime;
import java.time.ZoneId;

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
@DisplayName("06 - Allele Associations bulk upload")
@Order(6)
public class AlleleAssociationBulkUploadITCase extends BaseITCase {
	
	private String alleleCurie = "ALLELETEST:Allele0001";
	private String relationName = "is_allele_of";
	private String geneCurie = "GENETEST:Gene0001";
	private String reference = "AGRKB:000000001";
	private String evidenceCodeCurie = "DATEST:Evidence0001";
	
	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
				.httpClient(HttpClientConfig.httpClientConfig()
					.setParam("http.socket.timeout", 60000)
					.setParam("http.connection.timeout", 60000));
	}

	private final String alleleGeneAssociationBulkPostEndpoint = "/api/allelegeneassociation/bulk/WB/associationFile";
	private final String alleleGeneAssociationGetEndpoint = "/api/allelegeneassociation/findBy";
	private final String alleleGeneAssociationTestFilePath = "src/test/resources/bulk/A01_allele_association/";

	private void loadRequiredEntities() throws Exception {
	}
	
	@Test
	@Order(1)
	public void alleleBulkUploadCheckFields() throws Exception {
		loadRequiredEntities();
		
		checkSuccessfulBulkLoad(alleleGeneAssociationBulkPostEndpoint, alleleGeneAssociationTestFilePath + "AF_01_all_fields.json");
	
		RestAssured.given().
			when().
			get(alleleGeneAssociationGetEndpoint + "?alleleCurie=" + alleleCurie + "&relationName=" + relationName + "&geneCurie=" + geneCurie).
			then().
			statusCode(200).
			body("entity.subject.curie", is(alleleCurie)).
			body("entity.relation.name", is(relationName)).
			body("entity.object.curie", is(geneCurie)).
			body("entity.evidence", hasSize(1)).
			body("entity.evidence[0].curie", is(reference)).
			body("entity.evidenceCode.curie", is(evidenceCodeCurie)).
			body("entity.internal", is(true)).
			body("entity.obsolete", is(true)).
			body("entity.createdBy.uniqueId", is("ALLELETEST:Person0001")).
			body("entity.updatedBy.uniqueId", is("ALLELETEST:Person0002")).
			body("entity.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNote.internal", is(false)).
			body("entity.relatedNote.obsolete", is(true)).
			body("entity.relatedNote.updatedBy.uniqueId", is("DATEST:Person0002")).
			body("entity.relatedNote.createdBy.uniqueId", is("DATEST:Person0001")).
			body("entity.relatedNote.dateUpdated", is(OffsetDateTime.parse("2022-03-10T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNote.dateCreated", is(OffsetDateTime.parse("2022-03-09T22:10:12Z").atZoneSameInstant(ZoneId.systemDefault()).toOffsetDateTime().toString())).
			body("entity.relatedNote.freeText", is("Test note")).
			body("entity.relatedNote.noteType.name", is("comment")).
			body("entity.relatedNote.references[0].curie", is(reference));
	}
	
}
