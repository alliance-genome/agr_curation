package org.alliancegenome.curation_api.model.entities.slotAnnotations;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.InformationContentEntity;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "SlotAnnotation", description = "POJO that represents a SlotAnnotation")
@Table(indexes = { @Index(name = "slotannotation_createdby_index", columnList = "createdBy_id"), @Index(name = "slotannotation_updatedby_index", columnList = "updatedBy_id"), })
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class SlotAnnotation extends GeneratedAuditedObject {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index(name = "slotannotation_informationcontententity_slotannotation_id_index", columnList = "slotannotation_id"),
		@Index(name = "slotannotation_informationcontententity_evidence_curie_index", columnList = "evidence_curie"),

	})
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class, View.GeneView.class })
	private List<InformationContentEntity> evidence;

}
