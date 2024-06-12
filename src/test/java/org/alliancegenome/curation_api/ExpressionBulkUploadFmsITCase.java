package org.alliancegenome.curation_api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import lombok.extern.jbosslog.JBossLog;
import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.OntologyConstants;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("605 - Expression bulk upload - FMS")
@Order(605)
public class ExpressionBulkUploadFmsITCase extends BaseITCase {

	private final String expressionBulkPostEndpoint = "/api/expression-annotation/bulk/ZFIN/annotationFile";
	private final String expressionTestFilePath = "src/test/resources/bulk/fms/05_expression/";
	private final String expressionFindEndpoint = "/api/expression-annotation/find?limit=100&page=0";
	private final String taxon = "NCBITaxon:7955";
	private final String gene = "GEXPTEST:GENE001";
	private final String mmoTerm = "GEXPTEST:assay001";
	private final String referenceId = "ZFIN:PUB001";
	private final String publicationId = "PMID:009";

	@BeforeEach
	public void init() {
		RestAssured.config = RestAssuredConfig.config()
			.httpClient(HttpClientConfig.httpClientConfig()
				.setParam("http.socket.timeout", 100000)
				.setParam("http.connection.timeout", 100000));
	}

	@Test
	@Order(1)
	public void expressionBulkUploadAllFields() throws Exception {
		loadRequiredEntities();
		checkSuccessfulBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "AF_01_all_fields.json");
	}

	@Test
	@Order(2)
	public void expressionBulkUploadMissingRequiredFields() throws Exception {
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_01_no_geneId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_02_no_dateAssigned.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_03_no_evidence.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_04_no_assay.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_05_no_whenExpressed.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_06_no_whereExpressed.json");
	}

	@Test
	@Order(3)
	public void expressionBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_01_empty_geneId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_02_empty_dateAssigned.json");
	}

	@Test
	@Order(4)
	public void expressionBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_01_invalid_geneId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_02_invalid_dateAssigned.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_04_invalid_assay.json");

	}

	private void loadRequiredEntities() throws Exception {
		DataProvider dataProvider = createDataProvider("ZFIN", false);
		createNCBITaxonTerm(taxon, "Taxon", false);
		Vocabulary vocabulary1 = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm  symbolTerm = getVocabularyTerm(vocabulary1, "nomenclature_symbol");
		loadGenes(List.of(gene), taxon, symbolTerm, dataProvider);
		loadMMOTerm(mmoTerm, "assay001");
		ResourceDescriptor rd1 = createResourceDescriptor("ZFIN");
		createResourceDescriptorPage("homepage", "https://zfin.org/", rd1);
		createResourceDescriptorPage("reference", "https://zfin.org/[%s]", rd1);
		createReference(referenceId, false);
		createReference(publicationId, false);
		Vocabulary vocabulary2 = createVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, false);
		VocabularyTerm isExpressed = createVocabularyTerm(vocabulary2, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM, false);
	}
}
