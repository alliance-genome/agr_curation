package org.alliancegenome.curation_api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.alliancegenome.curation_api.enums.CurieSubdomain;
import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.model.entities.AGMDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AlleleDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.entities.interfaces.CurieInterface;
import org.junit.jupiter.api.Test;

/**
 * Pins the entity-to-subdomain mapping that CurieMintService.mintCurieIfAbsent(CurieInterface) now
 * derives instead of taking as an argument.
 *
 * Worth pinning because a wrong entry fails silently in the worst way: the write succeeds, the
 * entity gets a well-formed AGRKB curie, and the only symptom is that the id came out of another
 * entity's sequence. Nothing downstream would reject it.
 *
 * The expectations below are the subdomains the call sites named before the mapping moved into
 * CurieSubdomain, so this also guards the refactor itself.
 */
class CurieSubdomainTest {

	private static final Map<CurieInterface, MatiSubdomain> EXPECTED_BY_ENTITY = Map.of(
		new Gene(), MatiSubdomain.GENE,
		new Allele(), MatiSubdomain.ALLELE,
		new GeneMolecularInteraction(), MatiSubdomain.MOLECULAR_INTERACTION,
		new GeneGeneticInteraction(), MatiSubdomain.GENETIC_INTERACTION,
		new HTPExpressionDatasetAnnotation(), MatiSubdomain.HTP_EXPRESSION_DATASET,
		new HTPExpressionDatasetSampleAnnotation(), MatiSubdomain.HTP_EXPRESSION_SAMPLE
	);

	@Test
	void everyMintedEntityTypeResolvesToTheSubdomainItsCallSiteUsedToName() {
		EXPECTED_BY_ENTITY.forEach((entity, expected) ->
			assertEquals(expected, CurieSubdomain.subdomainFor(entity),
				entity.getClass().getSimpleName() + " resolves to the wrong MaTI subdomain"));
	}

	/**
	 * The three disease annotation services each pass their own concrete subtype, none of which is
	 * registered; they must all reach the single DISEASE_ANNOTATION entry on DiseaseAnnotation. This
	 * is the case the superclass walk exists for, and the same walk is what makes a Hibernate proxy
	 * resolve to the entity it stands in for.
	 */
	@Test
	void annotationSubtypesResolveThroughTheirRegisteredAncestor() {
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomain.subdomainFor(new AGMDiseaseAnnotation()));
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomain.subdomainFor(new AlleleDiseaseAnnotation()));
		assertEquals(MatiSubdomain.DISEASE_ANNOTATION, CurieSubdomain.subdomainFor(new GeneDiseaseAnnotation()));
		assertEquals(MatiSubdomain.PHENOTYPE_ANNOTATION, CurieSubdomain.subdomainFor(new AllelePhenotypeAnnotation()));
	}

	/**
	 * A Reference carries a curie inherited from CurieObject but the Alliance does not mint one for
	 * it on a write path, so it is deliberately unregistered. Resolution must fail loudly rather
	 * than fall back to some default subdomain and burn ids from the wrong sequence.
	 */
	@Test
	void unregisteredCurieCarrierIsRejectedRatherThanDefaulted() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
			() -> CurieSubdomain.subdomainFor(new Reference()));
		assertEquals(true, e.getMessage().contains("no MaTI subdomain is registered"),
			"unexpected message: " + e.getMessage());
	}

	@Test
	void nullEntityIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> CurieSubdomain.subdomainFor(null));
	}

	/**
	 * The backfill resolves its subdomain from the DAO's entity type rather than from an instance, so
	 * every DAO that extends BaseCurieSQLDAO must have its type argument registered. These are the
	 * fifteen type arguments behind the /system/mint*curies endpoints, with the subdomains those
	 * endpoints named before the mapping moved here.
	 *
	 * A miss on this path is the costliest kind: the endpoint would backfill an entire table out of
	 * another entity's sequence, or fail outright, rather than affecting one record.
	 */
	@Test
	void everyBackfillDaoEntityTypeResolvesToItsEndpointsSubdomain() {
		Map<Class<?>, MatiSubdomain> daoEntityTypes = Map.ofEntries(
			Map.entry(Gene.class, MatiSubdomain.GENE),
			Map.entry(Allele.class, MatiSubdomain.ALLELE),
			Map.entry(org.alliancegenome.curation_api.model.entities.Variant.class, MatiSubdomain.VARIANT),
			Map.entry(org.alliancegenome.curation_api.model.entities.AffectedGenomicModel.class, MatiSubdomain.AGM),
			Map.entry(org.alliancegenome.curation_api.model.entities.Construct.class, MatiSubdomain.CONSTRUCT),
			Map.entry(org.alliancegenome.curation_api.model.entities.Antibody.class, MatiSubdomain.ANTIBODY),
			Map.entry(org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent.class,
				MatiSubdomain.SEQUENCE_TARGETING_REAGENT),
			Map.entry(org.alliancegenome.curation_api.model.entities.AssemblyComponent.class,
				MatiSubdomain.ASSEMBLY_COMPONENT),
			Map.entry(org.alliancegenome.curation_api.model.entities.GenomeAssembly.class,
				MatiSubdomain.GENOME_ASSEMBLY),
			Map.entry(org.alliancegenome.curation_api.model.entities.DiseaseAnnotation.class,
				MatiSubdomain.DISEASE_ANNOTATION),
			Map.entry(org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation.class,
				MatiSubdomain.PHENOTYPE_ANNOTATION),
			Map.entry(GeneMolecularInteraction.class, MatiSubdomain.MOLECULAR_INTERACTION),
			Map.entry(GeneGeneticInteraction.class, MatiSubdomain.GENETIC_INTERACTION),
			Map.entry(HTPExpressionDatasetAnnotation.class, MatiSubdomain.HTP_EXPRESSION_DATASET),
			Map.entry(HTPExpressionDatasetSampleAnnotation.class, MatiSubdomain.HTP_EXPRESSION_SAMPLE));

		assertEquals(15, daoEntityTypes.size(), "one entry per /system/mint*curies endpoint");
		daoEntityTypes.forEach((entityClass, expected) ->
			assertEquals(expected, CurieSubdomain.subdomainForClass(entityClass),
				entityClass.getSimpleName() + " backfills from the wrong MaTI subdomain"));
	}

	@Test
	void everyRegisteredClassResolvesBackToItsOwnEntry() {
		for (CurieSubdomain cs : CurieSubdomain.values()) {
			assertEquals(cs, CurieSubdomain.forEntityClass(cs.getEntityClass()),
				cs.name() + " does not resolve back to itself; another entry shadows its class");
		}
	}

	@Test
	void unregisteredEntityClassIsRejected() {
		assertThrows(IllegalArgumentException.class, () -> CurieSubdomain.subdomainForClass(Reference.class));
		assertThrows(IllegalArgumentException.class, () -> CurieSubdomain.subdomainForClass(null));
	}

	/** Two entries pointing at the same MaTI subdomain would make minting ambiguous. */
	@Test
	void noSubdomainIsRegisteredTwice() {
		long distinct = java.util.Arrays.stream(CurieSubdomain.values())
			.map(CurieSubdomain::getSubdomain).distinct().count();
		assertEquals(CurieSubdomain.values().length, distinct,
			"a MaTI subdomain is registered against more than one entity class");
	}
}
