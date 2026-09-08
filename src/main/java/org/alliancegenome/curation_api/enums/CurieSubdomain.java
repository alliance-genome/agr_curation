package org.alliancegenome.curation_api.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Antibody;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.DiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneGeneticInteraction;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.model.entities.GenomeAssembly;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.PhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.interfaces.CurieInterface;

/**
 * Which MaTI subdomain each curie-carrying entity mints from.
 *
 * Every call site used to name its own subdomain, which meant the same entity-to-subdomain fact was
 * restated at each of them — 22 call sites for 10 entity types — with nothing to stop two of them
 * disagreeing, or a copy-pasted block minting an interaction into the allele sequence. That mapping
 * is a property of the entity, not of the call site, so it lives here once and
 * {@code CurieMintService.mintCurieIfAbsent(CurieInterface)} derives it.
 *
 * Registered against the class that identifies the subdomain, which is not always the class that
 * declares the curie field: disease and phenotype annotations both inherit theirs from
 * {@code Annotation}, and the two interactions from {@code GeneGeneAssociation}, so those are keyed
 * on the subtype that determines the subdomain instead.
 *
 * Entries exist for subdomains that have no mint-on-write path yet (variant, construct, antibody,
 * sequence targeting reagent, assembly component, genome assembly, phenotype annotation). Those are
 * reachable only through their backfill endpoints today; listing them here is what makes wiring one
 * up a one-line change at its validator rather than another subdomain constant to look up.
 *
 * Not every {@code CurieInterface} implementation belongs here. References, resources, people,
 * ontology terms and the GFF entities all inherit a curie from {@code CurieObject} without the
 * Alliance minting one for them, so they are deliberately absent and {@link #forEntity} rejects
 * them rather than guessing.
 */
public enum CurieSubdomain {

	GENE(Gene.class, MatiSubdomain.GENE),
	ALLELE(Allele.class, MatiSubdomain.ALLELE),
	VARIANT(Variant.class, MatiSubdomain.VARIANT),
	AGM(AffectedGenomicModel.class, MatiSubdomain.AGM),
	CONSTRUCT(Construct.class, MatiSubdomain.CONSTRUCT),
	ANTIBODY(Antibody.class, MatiSubdomain.ANTIBODY),
	SEQUENCE_TARGETING_REAGENT(SequenceTargetingReagent.class, MatiSubdomain.SEQUENCE_TARGETING_REAGENT),
	ASSEMBLY_COMPONENT(AssemblyComponent.class, MatiSubdomain.ASSEMBLY_COMPONENT),
	GENOME_ASSEMBLY(GenomeAssembly.class, MatiSubdomain.GENOME_ASSEMBLY),
	DISEASE_ANNOTATION(DiseaseAnnotation.class, MatiSubdomain.DISEASE_ANNOTATION),
	PHENOTYPE_ANNOTATION(PhenotypeAnnotation.class, MatiSubdomain.PHENOTYPE_ANNOTATION),
	MOLECULAR_INTERACTION(GeneMolecularInteraction.class, MatiSubdomain.MOLECULAR_INTERACTION),
	GENETIC_INTERACTION(GeneGeneticInteraction.class, MatiSubdomain.GENETIC_INTERACTION),
	HTP_EXPRESSION_DATASET(HTPExpressionDatasetAnnotation.class, MatiSubdomain.HTP_EXPRESSION_DATASET),
	HTP_EXPRESSION_SAMPLE(HTPExpressionDatasetSampleAnnotation.class, MatiSubdomain.HTP_EXPRESSION_SAMPLE);

	private final Class<? extends CurieInterface> entityClass;
	private final MatiSubdomain subdomain;

	CurieSubdomain(Class<? extends CurieInterface> entityClass, MatiSubdomain subdomain) {
		this.entityClass = entityClass;
		this.subdomain = subdomain;
	}

	/** The entity class this subdomain mints for. */
	public Class<? extends CurieInterface> getEntityClass() {
		return entityClass;
	}

	/** The MaTI subdomain to mint from. */
	public MatiSubdomain getSubdomain() {
		return subdomain;
	}

	// Enum constants are constructed before static initialisers run, so values() is safe here.
	private static final Map<Class<?>, CurieSubdomain> BY_ENTITY_CLASS =
		Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(cs -> cs.entityClass, cs -> cs));

	/**
	 * The registered subdomain for {@code entity}.
	 *
	 * Resolved by walking up from the entity's own class, so the nearest registered ancestor wins.
	 * That is what lets one DISEASE_ANNOTATION entry serve AGMDiseaseAnnotation,
	 * AlleleDiseaseAnnotation and GeneDiseaseAnnotation, and it means a Hibernate proxy — a
	 * generated subclass of the entity — resolves to the entity it stands in for rather than
	 * falling through.
	 *
	 * @throws IllegalArgumentException if nothing is registered for the entity. Deliberately loud:
	 *                                  returning null here, or defaulting to some subdomain, would
	 *                                  mint AGRKB ids into the wrong sequence or silently skip
	 *                                  minting, and both are worse than a failed write.
	 */
	public static CurieSubdomain forEntity(CurieInterface entity) {
		if (entity == null) {
			throw new IllegalArgumentException("cannot resolve a MaTI subdomain for a null entity");
		}
		return forEntityClass(entity.getClass());
	}

	/**
	 * The registered subdomain for an entity class, for callers that have the type but no instance —
	 * the backfill, which is driven by a DAO. Same nearest-registered-ancestor walk as
	 * {@link #forEntity}.
	 *
	 * @throws IllegalArgumentException if nothing is registered for the class
	 */
	public static CurieSubdomain forEntityClass(Class<?> entityClass) {
		if (entityClass == null) {
			throw new IllegalArgumentException("cannot resolve a MaTI subdomain for a null entity class");
		}
		for (Class<?> candidate = entityClass; candidate != null; candidate = candidate.getSuperclass()) {
			CurieSubdomain match = BY_ENTITY_CLASS.get(candidate);
			if (match != null) {
				return match;
			}
		}
		throw new IllegalArgumentException("no MaTI subdomain is registered for "
			+ entityClass.getName() + "; add it to " + CurieSubdomain.class.getSimpleName()
			+ " before minting AGRKB curies for it");
	}

	/** Shorthand for {@code forEntity(entity).getSubdomain()}. */
	public static MatiSubdomain subdomainFor(CurieInterface entity) {
		return forEntity(entity).getSubdomain();
	}

	/** Shorthand for {@code forEntityClass(entityClass).getSubdomain()}. */
	public static MatiSubdomain subdomainForClass(Class<?> entityClass) {
		return forEntityClass(entityClass).getSubdomain();
	}
}
