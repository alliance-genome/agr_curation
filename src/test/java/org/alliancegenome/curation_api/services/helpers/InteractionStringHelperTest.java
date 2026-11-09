package org.alliancegenome.curation_api.services.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.junit.jupiter.api.Test;

class InteractionStringHelperTest {

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

	private static PsiMiTabDTO dto() {
		PsiMiTabDTO dto = new PsiMiTabDTO();
		dto.setInteractionTypes(List.of("psi-mi:\"MI:0794\"(synthetic genetic interaction defined by inequality)"));
		dto.setExperimentalRoleA("psi-mi:\"MI:0499\"(unspecified role)");
		dto.setExperimentalRoleB("psi-mi:\"MI:0499\"(unspecified role)");
		dto.setInteractorAType("psi-mi:\"MI:0250\"(gene)");
		dto.setInteractorBType("psi-mi:\"MI:0250\"(gene)");
		dto.setSourceDatabaseIds(List.of("psi-mi:\"MI:0478\"(flybase)"));
		return dto;
	}

	private final Gene geneA = gene("AGRKB:102000000000001", "FB:FBgn0000001");
	private final Gene geneB = gene("AGRKB:102000000000002", "FB:FBgn0000002");
	private final List<Reference> references = List.of(reference("AGRKB:101000000000001"));

	@Test
	void geneticInteractionKeyUsesModIdsInExistingFormat() {
		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto(), geneA, geneB, null, references, new ArrayList<>(List.of("lethal")));

		assertEquals("FB:FBgn0000001|genetically_interacts_with|FB:FBgn0000002|AGRKB:101000000000001|MI:0794|MI:0499|MI:0499|MI:0250|MI:0250|MI:0478|lethal", uniqueId);
	}

	@Test
	void molecularInteractionKeyUsesModIdsAfterInteractionId() {
		PsiMiTabDTO dto = dto();
		dto.setInteractionDetectionMethods(List.of("psi-mi:\"MI:0018\"(two hybrid)"));

		String uniqueId = InteractionStringHelper.getGeneMolecularInteractionUniqueId(dto, geneA, geneB, "WB:WBInteraction0001", references);

		assertEquals("WB:WBInteraction0001|FB:FBgn0000001|physically_interacts_with|FB:FBgn0000002|AGRKB:101000000000001|MI:0794|MI:0499|MI:0499|MI:0250|MI:0250|MI:0018", uniqueId);
	}

	@Test
	void agrkbOnlyGeneFallsBackToCurie() {
		Gene agrkbOnly = gene("AGRKB:102000000000003", null);

		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto(), geneA, agrkbOnly, null, references, null);

		assertEquals("FB:FBgn0000001|genetically_interacts_with|AGRKB:102000000000003", uniqueId.substring(0, uniqueId.indexOf("|AGRKB:101")));
	}

	@Test
	void modInternalIdUsedWhenNoPrimaryExternalId() {
		Gene modInternalOnly = gene("AGRKB:102000000000004", null);
		modInternalOnly.setModInternalId("WB:WBGene00000004");

		String uniqueId = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto(), modInternalOnly, geneB, null, references, null);

		assertEquals("WB:WBGene00000004", uniqueId.split("\\|")[0]);
	}

	@Test
	void sharedParticipantsWithDifferentEvidenceGetDifferentKeys() {
		String first = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto(), geneA, geneB, null, references, null);
		String second = InteractionStringHelper.getGeneGeneticInteractionUniqueId(dto(), geneA, geneB, null, List.of(reference("AGRKB:101000000000002")), null);

		assertNotEquals(first, second);
	}
}
