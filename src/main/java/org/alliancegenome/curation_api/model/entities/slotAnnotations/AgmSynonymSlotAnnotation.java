package org.alliancegenome.curation_api.model.entities.slotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.12.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "GeneSynonymSlotAnnotation", description = "POJO representing an AGM synonym slot annotation")
public class AgmSynonymSlotAnnotation extends NameSlotAnnotation {

	@ManyToOne
	@JsonBackReference
	private AffectedGenomicModel singleAgm;

}
