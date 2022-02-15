package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
public class ExperimentalConditionOntologyTerm extends OntologyTerm {

}
