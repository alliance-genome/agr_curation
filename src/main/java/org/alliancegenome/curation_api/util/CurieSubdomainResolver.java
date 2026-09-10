package org.alliancegenome.curation_api.util;

import org.alliancegenome.curation_api.enums.MatiSubdomain;
import org.alliancegenome.curation_api.interfaces.CurieSubdomain;
import org.alliancegenome.curation_api.model.entities.interfaces.CurieInterface;

/**
 * Reads the {@link CurieSubdomain} declaration off an entity.
 *
 * Thin by design: {@code @Inherited} on the annotation already walks the class hierarchy, so all
 * this adds is the loud failure. That failure is the point — returning null, or defaulting to some
 * subdomain, would mint AGRKB ids out of another entity's sequence, and nothing downstream would
 * reject the result because the curie would be perfectly well-formed.
 */
public final class CurieSubdomainResolver {

	private CurieSubdomainResolver() {
	}

	/** The subdomain declared for {@code entity}'s type. */
	public static MatiSubdomain subdomainFor(CurieInterface entity) {
		if (entity == null) {
			throw new IllegalArgumentException("cannot resolve a MaTI subdomain for a null entity");
		}
		return subdomainForClass(entity.getClass());
	}

	/**
	 * The subdomain declared for an entity class, for callers holding a type rather than an instance
	 * — the backfill, which is driven by a DAO.
	 *
	 * @throws IllegalArgumentException if the class carries no {@link CurieSubdomain}, directly or
	 *                                  inherited
	 */
	public static MatiSubdomain subdomainForClass(Class<?> entityClass) {
		if (entityClass == null) {
			throw new IllegalArgumentException("cannot resolve a MaTI subdomain for a null entity class");
		}
		CurieSubdomain declared = entityClass.getAnnotation(CurieSubdomain.class);
		if (declared == null) {
			throw new IllegalArgumentException(entityClass.getName() + " declares no @"
				+ CurieSubdomain.class.getSimpleName()
				+ "; annotate it with the MaTI subdomain its AGRKB curies come from before minting for it");
		}
		return declared.value();
	}
}
