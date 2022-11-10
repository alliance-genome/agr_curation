package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "SlotAnnotation", description = "POJO that represents a SlotAnnotation")
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
public class SlotAnnotation extends GeneratedAuditedObject {
	
	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({View.FieldsAndLists.class})
	private List<InformationContentEntity> evidence;
	
}

