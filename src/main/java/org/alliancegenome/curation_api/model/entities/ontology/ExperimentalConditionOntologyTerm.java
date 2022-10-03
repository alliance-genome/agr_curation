package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion(min=LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max=LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies={OntologyTerm.class})
public class ExperimentalConditionOntologyTerm extends OntologyTerm {

}
