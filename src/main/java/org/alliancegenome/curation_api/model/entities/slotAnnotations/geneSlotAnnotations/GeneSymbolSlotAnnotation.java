package org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToOne;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.NameSlotAnnotation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.5.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { NameSlotAnnotation.class })
@Schema(name = "GeneSymbolSlotAnnotation", description = "POJO representing a gene symbol slot annotation")
public class GeneSymbolSlotAnnotation extends NameSlotAnnotation {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonBackReference
	private Gene singleGene;

}
