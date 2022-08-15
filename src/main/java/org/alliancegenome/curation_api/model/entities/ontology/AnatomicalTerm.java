package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion("1.2.1")
public class AnatomicalTerm extends OntologyTerm {

}
