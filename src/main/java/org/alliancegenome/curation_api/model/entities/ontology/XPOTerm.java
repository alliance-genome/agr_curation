package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import lombok.*;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion("1.2.1")
public class XPOTerm extends PhenotypeTerm {

}
