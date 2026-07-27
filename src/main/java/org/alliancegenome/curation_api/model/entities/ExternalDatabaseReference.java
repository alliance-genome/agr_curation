package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.InformationContentEntityTypeBridge;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.TypeBinderRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.TypeBinding;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@TypeBinding(binder = @TypeBinderRef(type = InformationContentEntityTypeBridge.class))
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "ExternalDatabaseReference: an external database reference")
@AGRCurationSchemaVersion(min = "2.10.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {InformationContentEntity.class})
@Table(indexes = {
	@Index(name = "externaldatabasereference_resourcedescriptorpage_index", columnList = "resourcedescriptorpage_id")
})
public class ExternalDatabaseReference extends InformationContentEntity {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class})
	@Fetch(FetchMode.SELECT)
	private ResourceDescriptorPage resourceDescriptorPage;

}
