package org.alliancegenome.curation_api.interfaces;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.alliancegenome.curation_api.enums.MatiSubdomain;

/**
 * Declares which MaTI subdomain an entity's AGRKB curies are minted from.
 *
 * Put it on the entity that determines the subdomain and {@code CurieMintService} picks it up, for
 * both the single mint on a create/upsert and the backfill. No call site names a subdomain, so the
 * fact is stated once, next to the entity it describes, rather than repeated at each of the 37
 * places that used to pass one.
 *
 * {@code @Inherited} is doing real work here. It means one declaration on an abstract parent covers
 * every subtype — {@code DiseaseAnnotation} serves AGM, allele and gene disease annotations, which
 * is what the services actually hand to the mint — and it means a Hibernate proxy, being a generated
 * subclass, resolves to the entity it stands in for. A subtype that needs a different subdomain
 * declares its own and overrides the parent, which is how the two interactions are split under
 * {@code GeneGeneAssociation}.
 *
 * Absence is meaningful, not an oversight to be defaulted away: References, ontology terms and the
 * GFF entities all inherit a curie from {@code CurieObject} without the Alliance minting one for
 * them, so they carry no annotation and {@code CurieSubdomainResolver} rejects them outright.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.TYPE)
public @interface CurieSubdomain {

	MatiSubdomain value();
}
