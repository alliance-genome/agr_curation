package org.alliancegenome.curation_api.model.entities.ontology;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = ZECOTerm.class, name = "ZECOTerm"),
	@JsonSubTypes.Type(value = XCOTerm.class, name = "XCOTerm")
})
@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { OntologyTerm.class })
public class ExperimentalConditionOntologyTerm extends OntologyTerm {

}
