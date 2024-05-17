package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.CurieObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(callSuper = true)
@JsonSubTypes({ @JsonSubTypes.Type(value = Reference.class, name = "Reference") })
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { CurieObject.class })
@Table(
	indexes = {
		@Index(name = "informationcontententity_curie_index", columnList = "curie"),
		@Index(name = "informationcontententity_createdby_index", columnList = "createdBy_id"),
		@Index(name = "informationcontententity_updatedby_index", columnList = "updatedBy_id"),
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "informationcontententity_curie_uk", columnNames = "curie")
	}
)
public class InformationContentEntity extends CurieObject {

}
