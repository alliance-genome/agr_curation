package org.alliancegenome.curation_api.model.entities.slotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Cassette;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** SCRUM-6535: the one current full name for the cassette. */
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.18.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "CassetteFullNameSlotAnnotation", description = "CassetteFullNameSlotAnnotation: the one current full name for the cassette")
public class CassetteFullNameSlotAnnotation extends NameSlotAnnotation {

	@OneToOne
	@JsonBackReference
	@Fetch(FetchMode.JOIN)
	private Cassette singleCassette;

}
