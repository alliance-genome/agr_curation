package org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "ConstructFullNameSlotAnnotation", description = "POJO representing a construct full name slot annotation")
@Table(indexes = { @Index(name = "constructfullname_singleconstruct_index", columnList = "singleconstruct_id") })
public class ConstructFullNameSlotAnnotation extends NameSlotAnnotation {

	@OneToOne
	@JsonBackReference
	private Construct singleConstruct;

}
