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
	@JsonSubTypes.Type(value = WBBTTerm.class, name = "WBBTTerm"),
	@JsonSubTypes.Type(value = CLTerm.class, name = "CLTerm"),
	@JsonSubTypes.Type(value = DAOTerm.class, name = "DAOTerm"),
	@JsonSubTypes.Type(value = MATerm.class, name = "MATerm"),
	@JsonSubTypes.Type(value = EMAPATerm.class, name = "EMAPATerm"),
	@JsonSubTypes.Type(value = UBERONTerm.class, name = "UBERONTerm"),
	@JsonSubTypes.Type(value = XBATerm.class, name = "XBATerm"),
	@JsonSubTypes.Type(value = ZFATerm.class, name = "ZFATerm")
})@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@AGRCurationSchemaVersion(min = LinkMLSchemaConstants.MIN_ONTOLOGY_RELEASE, max = LinkMLSchemaConstants.MAX_ONTOLOGY_RELEASE, dependencies = { OntologyTerm.class })
public class AnatomicalTerm extends OntologyTerm {

}
