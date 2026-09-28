package org.alliancegenome.curation_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.util.CurieSubdomainResolver;
import org.junit.jupiter.api.Test;

/**
 * Pins the @CurieSubdomain declarations that CurieMintService now reads off the entity instead of
 * taking as an argument.
 *
 * Worth pinning because a wrong or missing declaration fails silently in the worst way: the write
 * succeeds, the entity gets a well-formed AGRKB curie, and the only symptom is that the id came out
 * of another entity's sequence. Nothing downstream would reject it.
 *
 * The expectations below are the subdomains the call sites named before the mapping moved onto the
 * entities, so this guards the move as well as future edits.
 */
class CurieSubdomainTest {

	/**
	 * One entry per entity type that is minted for, which is also one per /system/mint*curies
	 * endpoint: the backfill resolves its subdomain from the DAO's entity type, so every type here
	 * must carry a declaration.
	 */
	private static final Map<Class<?>, MatiSubdomain> EXPECTED = Map.ofEntries(
		Map.entry(Gene.class, MatiSubdomain.GENE),
		Map.entry(Allele.class, MatiSubdomain.ALLELE),
		Map.entry(Variant.class, MatiSubdomain.VARIANT),
		Map.entry(AffectedGenomicModel.class, MatiSubdomain.AGM),
		Map.entry(Construct.class, MatiSubdomain.CONSTRUCT),
		Map.entry(Antibody.class, MatiSubdomain.ANTIBODY),
		Map.entry(SequenceTargetingReagent.class, MatiSubdomain.SEQUENCE_TARGETING_REAGENT),
		Map.entry(AssemblyComponent.class, MatiSubdomain.ASSEMBLY_COMPONENT),
		Map.entry(GenomeAssembly.class, MatiSubdomain.GENOME_ASSEMBLY),
		Map.entry(DiseaseAnnotation.class, MatiSubdomain.DISEASE_ANNOTATION),
		Map.entry(PhenotypeAnnotation.class, MatiSubdomain.PHENOTYPE_ANNOTATION),
		Map.entry(GeneMolecularInteraction.class, MatiSubdomain.MOLECULAR_INTERACTION),
		Map.entry(GeneGeneticInteraction.class, MatiSubdomain.GENETIC_INTERACTION),
		Map.entry(HTPExpressionDatasetAnnotation.class, MatiSubdomain.HTP_EXPRESSION_DATASET),
		Map.entry(HTPExpressionDatasetSampleAnnotation.class, MatiSubdomain.HTP_EXPRESSION_SAMPLE));

	@Test
	void everyMintedEntityTypeDeclaresTheSubdomainItsCallSiteUsedToName() {
		assertEquals(15, EXPECTED.size(), "one entry per minted entity type");
		EXPECTED.forEach((entityClass, expected) ->
			assertEquals(expected, CurieSubdomainResolver.subdomainForClass(entityClass),
				entityClass.getSimpleName() + " declares the wrong MaTI subdomain"));
	}

	/**
	 * The three disease annotation services each pass their own concrete subtype, none of which is
	 * annotated; they must all pick up the single declaration on DiseaseAnnotation. This is what
	 * {@code @Inherited} buys, and the same mechanism is what makes a Hibernate proxy — a generated
	 * subclass — resolve to the entity it stands in for.
	 */
	@Test
	void subtypesInheritTheirParentsDeclaration() {
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomainResolver.subdomainFor(new AGMDiseaseAnnotation()));
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomainResolver.subdomainFor(new AlleleDiseaseAnnotation()));
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomainResolver.subdomainFor(new GeneDiseaseAnnotation()));
		assertEquals(MatiSubdomain.PHENOTYPE_ANNOTATION, CurieSubdomainResolver.subdomainFor(new AllelePhenotypeAnnotation()));
	}

	/**
	 * Both interactions descend from GeneGeneAssociation, which carries no declaration, and each
	 * declares its own. If one ever inherited the other's, minting would cross the two sequences.
	 */
	@Test
	void siblingInteractionsKeepTheirOwnSubdomains() {
		assertEquals(MatiSubdomain.MOLECULAR_INTERACTION, CurieSubdomainResolver.subdomainFor(new GeneMolecularInteraction()));
		assertEquals(MatiSubdomain.GENETIC_INTERACTION, CurieSubdomainResolver.subdomainFor(new GeneGeneticInteraction()));
	}

	/**
	 * A Reference carries a curie inherited from CurieObject but the Alliance does not mint one for
	 * it, so it is deliberately unannotated. Resolution must fail loudly rather than default to some
	 * subdomain and burn ids from the wrong sequence.
	 */
	@Test
	void unannotatedCurieCarrierIsRejectedRatherThanDefaulted() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> CurieSubdomainResolver.subdomainFor(new Reference()));
		assertEquals(true, e.getMessage().contains("declares no @CurieSubdomain"),
			"unexpected message: " + e.getMessage());
		assertThrows(IllegalArgumentException.class,
			() -> CurieSubdomainResolver.subdomainForClass(Reference.class));
	}

	@Test
	void nullsAreRejected() {
		assertThrows(IllegalArgumentException.class, () -> CurieSubdomainResolver.subdomainFor(null));
		assertThrows(IllegalArgumentException.class, () -> CurieSubdomainResolver.subdomainForClass(null));
	}

	/**
	 * Two entities declaring the same subdomain would make minting ambiguous. Checked over the types
	 * this test knows about rather than by scanning the classpath, so adding a minted entity means
	 * adding it to EXPECTED above.
	 */
	@Test
	void noSubdomainIsDeclaredTwice() {
		long distinct = EXPECTED.values().stream().distinct().count();
		assertEquals(EXPECTED.size(), distinct,
			"a MaTI subdomain is declared on more than one entity class");
	}
}
