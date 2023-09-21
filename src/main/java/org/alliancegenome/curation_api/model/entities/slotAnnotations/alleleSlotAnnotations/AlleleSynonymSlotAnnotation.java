package org.alliancegenome.curation_api.model.entities.slotAnnotations.alleleSlotAnnotations;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
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
@Schema(name = "AlleleSynonymSlotAnnotation", description = "POJO representing an allele synonym slot annotation")
@Table(indexes = { @Index(name = "allelesynonym_singleallele_curie_index", columnList = "singleallele_curie"), })
public class AlleleSynonymSlotAnnotation extends NameSlotAnnotation {

	@ManyToOne
	@JsonBackReference
	@Fetch(FetchMode.JOIN)
	private Allele singleAllele;

}
