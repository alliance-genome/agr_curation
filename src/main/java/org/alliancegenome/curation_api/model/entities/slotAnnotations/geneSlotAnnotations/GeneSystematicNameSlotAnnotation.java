package org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "GeneSystematicNameSlotAnnotation", description = "POJO representing a gene systematic name slot annotation")
@Table(indexes = { @Index(name = "genesystematicname_singlegene_curie_index", columnList = "singlegene_curie"), })
public class GeneSystematicNameSlotAnnotation extends NameSlotAnnotation {

	@OneToOne
	@JsonBackReference
	private Gene singleGene;

}
