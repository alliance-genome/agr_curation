package org.alliancegenome.curation_api;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import org.alliancegenome.curation_api.base.BaseITCase;
import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.resources.TestContainerResource;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@QuarkusIntegrationTest
@QuarkusTestResource(TestContainerResource.Initializer.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("606 - Expression bulk upload - FMS")
@Order(606)
public class ExpressionBulkUploadFmsITCase extends BaseITCase {

	private final String expressionBulkPostEndpoint = "/api/gene-expression-annotation/bulk/ZFIN/annotationFile";
	private final String expressionTestFilePath = "src/test/resources/bulk/fms/07_expression/";
	private final String expressionFindEndpoint = "/api/gene-expression-annotation/find?limit=100&page=0";
	private final String taxon = "NCBITaxon:7955";
	private final String gene = "GEXPTEST:GENE001";
	private final String mmoTerm = "GEXPTEST:assay001";
	private final String referenceId = "ZFIN:PUB001";
	private final String agrReferenceId = "AGRKB:101000000668377";
	private final String publicationId = "PMID:009";
	private final String agrPublicationId = "AGRKB:101000000668376";

	private final String stageTermId = "ZFS:001";
	private final String stageUberonTermId = "UBERON:001";

	private final String anatomicalStructureTermId = "ANAT:001";
	private final String anatomicalSubstructureTermId = "ANAT:002";
	private final String cellularComponentTermId = "GOTEST:0012";
	private final String anatomicalStructureQualifierTermId = "UBERON:002";
	private final String anatomicalSubstructureQualifierTermId = "UBERON:003";
	private final String cellularComponentQualifierTermId = "FBCV:001";
	private final String anatomicalStructureUberonTermId1 = "UBERON:004";
	private final String anatomicalStructureUberonTermId2 = "UBERON:005";
	private final String anatomicalSubstructureUberonTermId1 = "UBERON:006";
	private final String anatomicalSubstructureUberonTermId2 = "UBERON:007";

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
		RestAssured.given().when()
			.header("Content-Type", "application/json")
			.body("{}")
			.post(expressionFindEndpoint)
			.then()
			.statusCode(200)
			.body("totalResults", is(1))
			.body("results", hasSize(1))
			.body("results[0].type", is("GeneExpressionAnnotation"))
			.body("results[0].dateCreated", is("2024-01-17T15:31:34Z"))
			.body("results[0].dataProvider.sourceOrganization.abbreviation", is("ZFIN"))
			.body("results[0].expressionAnnotationSubject.modEntityId", is(gene))
			.body("results[0].expressionAssayUsed.curie", is(mmoTerm))
			.body("results[0].whereExpressedStatement", is("trunk"))
			.body("results[0].whenExpressedStageName", is("stage1"))
			.body("results[0].singleReference.crossReferences[0].referencedCurie", is(publicationId))
			.body("results[0].relation.name", is(VocabularyConstants.GENE_EXPRESSION_RELATION_TERM))
			.body("results[0].expressionPattern.whenExpressed.developmentalStageStart.curie", is(stageTermId))
			.body("results[0].expressionPattern.whenExpressed.stageUberonSlimTerms[0].name", is(stageUberonTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructure.curie", is(anatomicalStructureTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructure.curie", is(anatomicalSubstructureTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentTerm.curie", is(cellularComponentTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureQualifiers[0].name", is(anatomicalStructureQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureQualifiers[0].name", is(anatomicalSubstructureQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.cellularComponentQualifiers[0].name", is(cellularComponentQualifierTermId))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureUberonTerms[0].curie", is(anatomicalStructureUberonTermId1))
			.body("results[0].expressionPattern.whereExpressed.anatomicalStructureUberonTerms[1].curie", is(anatomicalStructureUberonTermId2))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureUberonTerms[0].curie", is(anatomicalSubstructureUberonTermId1))
			.body("results[0].expressionPattern.whereExpressed.anatomicalSubstructureUberonTerms[1].curie", is(anatomicalSubstructureUberonTermId2));
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
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_07_nowhenExpressedStageName.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_08_nowhereExpressedStatement.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "MR_09_norCellComponentNORanatStructure.json");
	}

	@Test
	@Order(3)
	public void expressionBulkUploadEmptyRequiredFields() throws Exception {
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_01_empty_geneId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_02_empty_dateAssigned.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_03_empty_assay.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_04_empty_whenExpressedStageName.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "ER_05_empty_whereExpressedStatement.json");
	}

	@Test
	@Order(4)
	public void expressionBulkUploadInvalidFields() throws Exception {
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_01_invalid_geneId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_02_invalid_dateAssigned.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_03_invalid_assay.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_04_invalid_publicationId.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_05_invalid_stageterm.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_06_invalid_anatomical_structure.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_07_invalid_anatomical_substructure.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_08_invalid_cellularcomponent.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_09_invalid_anatomicalstructurequalifier.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_10_invalid_anatomicalsubstructurequalifier.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_11_invalid_cellularcomponentqualifier.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_12_invalid_anatomicalstructureuberonslimterms.json");
		checkFailedBulkLoad(expressionBulkPostEndpoint, expressionTestFilePath + "IV_13_invalid_anatomicalsubstructureuberonslimterms.json");
	}

	private void loadRequiredEntities() throws Exception {
		DataProvider dataProvider = createDataProvider("ZFIN", false);
		Vocabulary vocabulary1 = getVocabulary(VocabularyConstants.NAME_TYPE_VOCABULARY);
		VocabularyTerm symbolTerm = getVocabularyTerm(vocabulary1, "nomenclature_symbol");
		loadGenes(List.of(gene), taxon, symbolTerm, dataProvider);
		loadMMOTerm(mmoTerm, "assay001");
		ResourceDescriptor rd1 = createResourceDescriptor("ZFIN");
		createResourceDescriptorPage("homepage", "https://zfin.org/", rd1);
		createResourceDescriptorPage("reference", "https://zfin.org/[%s]", rd1);
		loadReference(agrPublicationId, publicationId);
		loadReference(agrReferenceId, referenceId);
		Vocabulary vocabulary2 = createVocabulary(VocabularyConstants.GENE_EXPRESSION_VOCABULARY, false);
		VocabularyTerm isExpressed = createVocabularyTerm(vocabulary2, VocabularyConstants.GENE_EXPRESSION_RELATION_TERM, false);
		Vocabulary stageUberonTermVocabulary = getVocabulary(VocabularyConstants.STAGE_UBERON_SLIM_TERMS);
		Vocabulary anatomicalStructureUberonTermVocabulary = getVocabulary(VocabularyConstants.ANATOMICAL_STRUCTURE_UBERON_SLIM_TERMS);
		Vocabulary cellullarComponentQualififerVocabulary = getVocabulary(VocabularyConstants.CELLULAR_COMPONENT_QUALIFIERS);
		loadStageTerm(stageTermId, "StageTermTest");
		createVocabularyTerm(stageUberonTermVocabulary, stageUberonTermId, false);
		loadAnatomyTerm(anatomicalStructureTermId, "AnatomicalStructureTermTest");
		loadAnatomyTerm(anatomicalSubstructureTermId, "AnatomicalSubStructureTermTest");
		loadGOTerm(cellularComponentTermId, "CellularComponentTermTest");
		createVocabularyTerm(anatomicalStructureUberonTermVocabulary, anatomicalStructureQualifierTermId, false);
		createVocabularyTerm(anatomicalStructureUberonTermVocabulary, anatomicalSubstructureQualifierTermId, false);
		createVocabularyTerm(cellullarComponentQualififerVocabulary, cellularComponentQualifierTermId, false);
		loadUberonTerm(anatomicalStructureUberonTermId1, "UberonTermTest1");
		loadUberonTerm(anatomicalStructureUberonTermId2, "UberonTermTest2");
		loadUberonTerm(anatomicalSubstructureUberonTermId1, "UberonTermTest3");
		loadUberonTerm(anatomicalSubstructureUberonTermId2, "UberonTermTest4");
	}
}
