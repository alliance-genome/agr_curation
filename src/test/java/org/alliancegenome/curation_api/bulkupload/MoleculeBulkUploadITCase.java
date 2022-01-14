package org.alliancegenome.curation_api.bulkupload;

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
@Order(5)
public class MoleculeBulkUploadITCase {

    @BeforeEach
    public void init() {
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 100000)
                    .setParam("http.connection.timeout", 100000));
    }

    @Test
    @Order(1)
    public void moleculeBulkUploadCheckModExamples() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/00_mod_examples.json"));

        // upload file
        RestAssured.given().
            contentType("application/json").
            body(content).
            when().
            post("/api/molecule/bulk/moleculefile").
            then().
            statusCode(200);

        // check if all entries except those with CHEBI ID are loaded
       RestAssured.given().
            when().
            header("Content-Type", "application/json").
            body("{}").
            post("/api/molecule/find?limit=20&page=0").
            then().
            statusCode(200).
               body("totalResults", is(6)).
               body("results", hasSize(6)).
               body("results[0].curie", is("WB:WBMol:00001323")).
               body("results[1].curie", is("WB:WBMol:00006523")).
               body("results[2].curie", is("WB:WBMol:00007786")).
               body("results[3].curie", is("WB:WBMol:00007787")).
               body("results[4].curie", is("WB:WBMol:00007847")).
               body("results[5].curie", is("WB:WBMol:00000002"));
    }

    @Test
    @Order(2)
    public void moleculeBulkUploadCheckFields() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/01_all_fields.json"));

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
                body("totalResults", is(7)).
                body("results", hasSize(7)).
                body("results[6].curie", is("TEST:TestMol00000001")).
                body("results[6].name", is("Test molecule 1")).
                body("results[6].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[6].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[6].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[6].formula", is("C15H20O4")).
                body("results[6].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[6].synonyms[0]", is("TM1")).
                body("results[6].synonyms[1]", is("TestMol1")).
                body("results[6].synonyms", hasSize(2)).
                body("results[6].crossReferences[0].curie", is("TEST:TestMol00000001")).
                body("results[6].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[6].crossReferences", hasSize(1));
    }

    @Test
    @Order(3)
    public void moleculeBulkUploadNoId() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/02_no_id.json"));

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
                body("totalResults", is(7)).
                body("results", hasSize(7));
    }

    @Test
    @Order(4)
    public void moleculeBulkUploadNoName() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/03_no_name.json"));

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
                body("totalResults", is(7)).
                body("results", hasSize(7));
    }
    
    @Test
    @Order(5)
    public void moleculeBulkUploadNoInchi() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/04_no_inchi.json"));

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
                body("totalResults", is(8)).
                body("results", hasSize(8)).
                body("results[7].curie", is("TEST:TestMol00000004")).
                body("results[7].name", is("Test molecule 4")).
                body("results[7].inchi", is(nullValue())).
                body("results[7].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[7].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[7].formula", is("C15H20O4")).
                body("results[7].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[7].synonyms[0]", is("TM4")).
                body("results[7].synonyms[1]", is("TestMol4")).
                body("results[7].synonyms", hasSize(2)).
                body("results[7].crossReferences[0].curie", is("TEST:TestMol00000004")).
                body("results[7].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[7].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(6)
    public void moleculeBulkUploadNoInchiKey() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/05_no_inchikey.json"));

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
                body("totalResults", is(9)).
                body("results", hasSize(9)).
                body("results[8].curie", is("TEST:TestMol00000005")).
                body("results[8].name", is("Test molecule 5")).
                body("results[8].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[8].inchiKey", is(nullValue())).
                body("results[8].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[8].formula", is("C15H20O4")).
                body("results[8].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[8].synonyms[0]", is("TM5")).
                body("results[8].synonyms[1]", is("TestMol5")).
                body("results[8].synonyms", hasSize(2)).
                body("results[8].crossReferences[0].curie", is("TEST:TestMol00000005")).
                body("results[8].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[8].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(7)
    public void moleculeBulkUploadNoIupac() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/06_no_iupac.json"));

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
                body("totalResults", is(10)).
                body("results", hasSize(10)).
                body("results[9].curie", is("TEST:TestMol00000006")).
                body("results[9].name", is("Test molecule 6")).
                body("results[9].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[9].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[9].iupac", is(nullValue())).
                body("results[9].formula", is("C15H20O4")).
                body("results[9].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[9].synonyms[0]", is("TM6")).
                body("results[9].synonyms[1]", is("TestMol6")).
                body("results[9].synonyms", hasSize(2)).
                body("results[9].crossReferences[0].curie", is("TEST:TestMol00000006")).
                body("results[9].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[9].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(8)
    public void moleculeBulkUploadNoFormula() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/07_no_formula.json"));

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
                body("totalResults", is(11)).
                body("results", hasSize(11)).
                body("results[10].curie", is("TEST:TestMol00000007")).
                body("results[10].name", is("Test molecule 7")).
                body("results[10].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[10].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[10].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[10].formula", is(nullValue())).
                body("results[10].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[10].synonyms[0]", is("TM7")).
                body("results[10].synonyms[1]", is("TestMol7")).
                body("results[10].synonyms", hasSize(2)).
                body("results[10].crossReferences[0].curie", is("TEST:TestMol00000007")).
                body("results[10].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[10].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(9)
    public void moleculeBulkUploadNoSmiles() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/08_no_smiles.json"));

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
                body("totalResults", is(12)).
                body("results", hasSize(12)).
                body("results[11].curie", is("TEST:TestMol00000008")).
                body("results[11].name", is("Test molecule 8")).
                body("results[11].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[11].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[11].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[11].formula", is("C15H20O4")).
                body("results[11].smiles", is(nullValue())).
                body("results[11].synonyms[0]", is("TM8")).
                body("results[11].synonyms[1]", is("TestMol8")).
                body("results[11].synonyms", hasSize(2)).
                body("results[11].crossReferences[0].curie", is("TEST:TestMol00000008")).
                body("results[11].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[11].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(10)
    public void moleculeBulkUploadNoSynonyms() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/09_no_synonyms.json"));

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
                body("totalResults", is(13)).
                body("results", hasSize(13)).
                body("results[12].curie", is("TEST:TestMol00000009")).
                body("results[12].name", is("Test molecule 9")).
                body("results[12].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[12].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[12].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[12].formula", is("C15H20O4")).
                body("results[12].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[12].synonyms", is(nullValue())).
                body("results[12].crossReferences[0].curie", is("TEST:TestMol00000009")).
                body("results[12].crossReferences[0].pageAreas[0]", is("molecule")).
                body("results[12].crossReferences", hasSize(1));
    }
    
    @Test
    @Order(11)
    public void moleculeBulkUploadNoCrossReferences() throws Exception {
        String content = Files.readString(Path.of("src/test/resources/bulk/05_molecule/10_no_cross_references.json"));

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
                body("totalResults", is(14)).
                body("results", hasSize(14)).
                body("results[13].curie", is("TEST:TestMol00000010")).
                body("results[13].name", is("Test molecule 10")).
                body("results[13].inchi", is("InChI=1S/C15H20O4/c1-10(7-13(17)18)5-6-15(19)11(2)8-12(16)9-14(15,3)4/h5-8,19H,9H2,1-4H3,(H,17,18)/b6-5+,10-7-/t15-/m1/s1")).
                body("results[13].inchiKey", is("JLIDBLDQVAYHNE-YKALOCIXSA-N")).
                body("results[13].iupac", is("(2Z,4E)-5-[(1S)-1-hydroxy-2,6,6-trimethyl-4-oxocyclohex-2-en-1-yl]-3-methylpenta-2,4-dienoic acid")).
                body("results[13].formula", is("C15H20O4")).
                body("results[13].smiles", is("CC(\\\\C=C\\\\[C@@]1(O)C(C)=CC(=O)CC1(C)C)=C\\\\C(O)=O")).
                body("results[13].synonyms[0]", is("TM10")).
                body("results[13].synonyms[1]", is("TestMol10")).
                body("results[13].synonyms", hasSize(2)).
                body("results[13].crossReferences", is(nullValue()));
    }
}
