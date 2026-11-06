package org.alliancegenome.curation_api.services.validation.dto.fms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;

import org.alliancegenome.curation_api.dao.GeneGeneticInteractionDAO;
import org.alliancegenome.curation_api.dao.GeneMolecularInteractionDAO;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.AlleleService;
import org.alliancegenome.curation_api.services.CurieMintService;
import org.alliancegenome.curation_api.services.GeneGeneticInteractionService;
import org.alliancegenome.curation_api.services.GeneMolecularInteractionService;
import org.alliancegenome.curation_api.services.VocabularyTermService;
import org.alliancegenome.curation_api.services.helpers.InteractionAnnotationsHelper;
import org.alliancegenome.curation_api.services.helpers.InteractionStringHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneInteractionUniqueIdLookupTest {

	private static final String INTERACTION_ID = "WB:WBInteraction0001";

	private final Gene geneA = gene("AGRKB:102000000000001", "FB:FBgn0000001");
	private final Gene geneB = gene("AGRKB:102000000000002", "FB:FBgn0000002");
	private final List<Reference> references = List.of(reference("AGRKB:101000000000001"));

	private GeneGeneticInteractionFmsDTOValidator geneticValidator;
	private GeneGeneticInteractionService geneticService;
	private GeneMolecularInteractionFmsDTOValidator molecularValidator;
	private GeneMolecularInteractionService molecularService;

	private static Gene gene(String curie, String primaryExternalId) {
		Gene gene = new Gene();
		gene.setCurie(curie);
		gene.setPrimaryExternalId(primaryExternalId);
		return gene;
	}

	private static Reference reference(String curie) {
		Reference reference = new Reference();
		reference.setCurie(curie);
		return reference;
	}

	private static PsiMiTabDTO dto(String interactionId) {
		PsiMiTabDTO dto = new PsiMiTabDTO();
		dto.setInteractorAIdentifier("flybase:FBgn0000001");
		dto.setInteractorBIdentifier("flybase:FBgn0000002");
		dto.setInteractionTypes(List.of("psi-mi:\"MI:0794\"(synthetic genetic interaction defined by inequality)"));
		if (interactionId != null) {
			dto.setInteractionIds(List.of("wormbase:" + interactionId.substring(3)));
		}
		return dto;
	}

	@BeforeEach
	void setUp() {
		geneticValidator = spy(new GeneGeneticInteractionFmsDTOValidator());
		geneticService = mock(GeneGeneticInteractionService.class);
		geneticValidator.geneGeneticInteractionService = geneticService;
		geneticValidator.geneGeneticInteractionDAO = mock(GeneGeneticInteractionDAO.class);
		geneticValidator.vocabularyTermService = mock(VocabularyTermService.class);
		geneticValidator.interactionAnnotationsHelper = mock(InteractionAnnotationsHelper.class);
		geneticValidator.curieMintService = mock(CurieMintService.class);
		geneticValidator.alleleService = mock(AlleleService.class);
		stubCommon(geneticValidator, geneticValidator.vocabularyTermService);
		when(geneticValidator.geneGeneticInteractionDAO.persist(any(GeneGeneticInteraction.class))).thenAnswer(inv -> inv.getArgument(0));
		when(geneticService.getByIdentifier(anyString())).thenReturn(new ObjectResponse<>());

		molecularValidator = spy(new GeneMolecularInteractionFmsDTOValidator());
		molecularService = mock(GeneMolecularInteractionService.class);
		molecularValidator.geneMolecularInteractionService = molecularService;
		molecularValidator.geneMolecularInteractionDAO = mock(GeneMolecularInteractionDAO.class);
		molecularValidator.vocabularyTermService = mock(VocabularyTermService.class);
		molecularValidator.curieMintService = mock(CurieMintService.class);
		stubCommon(molecularValidator, molecularValidator.vocabularyTermService);
		when(molecularValidator.geneMolecularInteractionDAO.persist(any(GeneMolecularInteraction.class))).thenAnswer(inv -> inv.getArgument(0));
		when(molecularService.getByIdentifier(anyString())).thenReturn(new ObjectResponse<>());
	}

	private void stubCommon(GeneInteractionFmsDTOValidator validator, VocabularyTermService vocabularyTermService) {
		doReturn(new ObjectResponse<>(references)).when(validator).validateReferences(any());
		doReturn(new ObjectResponse<>(geneA)).when(validator).findAllianceGene("flybase:FBgn0000001", null);
		doReturn(new ObjectResponse<>(geneB)).when(validator).findAllianceGene("flybase:FBgn0000002", null);
		doAnswer(inv -> new ObjectResponse<>(inv.getArgument(0))).when(validator).validateGeneInteraction(any(), any(), any());
		when(vocabularyTermService.getTermInVocabularyTermSet(anyString(), anyString())).thenReturn(new ObjectResponse<>(new VocabularyTerm()));
	}

	private String geneticKey(PsiMiTabDTO dto, String interactionId) {
		return InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto, geneA, geneB, interactionId, references, null);
	}

	private String molecularKey(PsiMiTabDTO dto, String interactionId) {
		return InteractionStringHelper.getGeneMolecularInteractionUniqueId(dto, geneA, geneB, interactionId, references);
	}

	private static GeneGeneticInteraction storedGenetic(String uniqueId) {
		GeneGeneticInteraction stored = new GeneGeneticInteraction();
		stored.setId(42L);
		stored.setCurie("AGRKB:103000000000042");
		stored.setUniqueId(uniqueId);
		return stored;
	}

	private static GeneMolecularInteraction storedMolecular(String uniqueId) {
		GeneMolecularInteraction stored = new GeneMolecularInteraction();
		stored.setId(43L);
		stored.setCurie("AGRKB:103000000000043");
		stored.setUniqueId(uniqueId);
		return stored;
	}

	@Test
	void geneticWithoutInteractionIdMatchesStoredModKey() throws Exception {
		PsiMiTabDTO dto = dto(null);
		String modKey = geneticKey(dto, null);
		GeneGeneticInteraction stored = storedGenetic(modKey);
		when(geneticService.getByIdentifier(modKey)).thenReturn(new ObjectResponse<>(stored));

		GeneGeneticInteraction result = geneticValidator.validateGeneGeneticInteractionFmsDTO(dto).getEntity();

		assertSame(stored, result);
		assertEquals(modKey, result.getUniqueId());
	}

	@Test
	void geneticWithoutInteractionIdAndNoStoredRowCreatesNewWithModKey() throws Exception {
		PsiMiTabDTO dto = dto(null);

		GeneGeneticInteraction result = geneticValidator.validateGeneGeneticInteractionFmsDTO(dto).getEntity();

		assertNull(result.getId());
		assertEquals(geneticKey(dto, null), result.getUniqueId());
	}

	@Test
	void geneticWithInteractionIdMatchesOnInteractionIdAndUpdatesItsKey() throws Exception {
		PsiMiTabDTO dto = dto(INTERACTION_ID);
		GeneGeneticInteraction stored = storedGenetic("previous key");
		when(geneticService.getByIdentifier(INTERACTION_ID)).thenReturn(new ObjectResponse<>(stored));

		GeneGeneticInteraction result = geneticValidator.validateGeneGeneticInteractionFmsDTO(dto).getEntity();

		assertSame(stored, result);
		assertEquals(geneticKey(dto, INTERACTION_ID), result.getUniqueId());
	}

	@Test
	void geneticWithoutInteractionIdDoesNotMatchRowWithDifferentEvidence() throws Exception {
		PsiMiTabDTO dto = dto(null);
		String otherEvidenceKey = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto, geneA, geneB, null, List.of(reference("AGRKB:101000000000002")), null);
		when(geneticService.getByIdentifier(otherEvidenceKey)).thenReturn(new ObjectResponse<>(storedGenetic(otherEvidenceKey)));

		GeneGeneticInteraction result = geneticValidator.validateGeneGeneticInteractionFmsDTO(dto).getEntity();

		assertNull(result.getId());
	}

	@Test
	void molecularWithoutInteractionIdMatchesStoredModKey() throws Exception {
		PsiMiTabDTO dto = dto(null);
		String modKey = molecularKey(dto, null);
		GeneMolecularInteraction stored = storedMolecular(modKey);
		when(molecularService.getByIdentifier(modKey)).thenReturn(new ObjectResponse<>(stored));

		GeneMolecularInteraction result = molecularValidator.validateGeneMolecularInteractionFmsDTO(dto).getEntity();

		assertSame(stored, result);
	}

	@Test
	void molecularWithInteractionIdMatchesOnInteractionIdAndUpdatesItsKey() throws Exception {
		PsiMiTabDTO dto = dto(INTERACTION_ID);
		GeneMolecularInteraction stored = storedMolecular("previous key");
		when(molecularService.getByIdentifier(INTERACTION_ID)).thenReturn(new ObjectResponse<>(stored));

		GeneMolecularInteraction result = molecularValidator.validateGeneMolecularInteractionFmsDTO(dto).getEntity();

		assertSame(stored, result);
		assertEquals(molecularKey(dto, INTERACTION_ID), result.getUniqueId());
	}
}
