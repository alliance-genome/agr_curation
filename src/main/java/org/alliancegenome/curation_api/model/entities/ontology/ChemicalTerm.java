package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class ChemicalTerm extends OntologyTerm {

}
