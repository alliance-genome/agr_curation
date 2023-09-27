package org.alliancegenome.curation_api.model.entities.slotAnnotations.constructSlotAnnotations;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Construct;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "ConstructSymbolSlotAnnotation", description = "POJO representing a construct symbol slot annotation")
@Table(indexes = { @Index(name = "constructsymbol_singleconstruct_curie_index", columnList = "singleconstruct_curie"), })
public class ConstructSymbolSlotAnnotation extends NameSlotAnnotation {

	@OneToOne
	@JsonBackReference
	private Construct singleConstruct;

}
