package org.alliancegenome.curation_api.model.entities;

import java.util.List;

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

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "association", description = "Annotation class representing a disease annotation")
@AGRCurationSchemaVersion(min = "1.3.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })

@Table(indexes = { 
	@Index(name = "DiseaseAnnotation_createdby_index", columnList = "createdBy_id"), 
	@Index(name = "DiseaseAnnotation_updatedby_index", columnList = "updatedBy_id"),
})

public class Association extends GeneratedAuditedObject {

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	private List<InformationContentEntity> evidence;

}
