package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import lombok.*;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class DAOTerm extends OntologyTerm {

}
