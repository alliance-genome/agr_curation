package org.alliancegenome.curation_api.model.entities.slotAnnotations.sqtrSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.7.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { SlotAnnotation.class })
@Schema(name = "SQTRSecondaryIdSlotAnnotation", description = "POJO representing a SQTR secondary ID slot annotation")
@Table(indexes = { @Index(name = "sqtrsecondaryid_singlesqtr_index", columnList = "singlesqtr_id"), })
public class SQTRSecondaryIdSlotAnnotation extends SecondaryIdSlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private String secondaryId;

}
