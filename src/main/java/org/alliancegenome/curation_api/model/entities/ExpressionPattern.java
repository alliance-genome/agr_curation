package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.CurationView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "2.2.3", max = LinkMLSchemaConstants.LATEST_RELEASE)
@Schema(name = "Expression_Pattern", description = "Annotation class representing an expression pattern")

@Table(indexes = {
	@Index(name = "expressionpattern_whenexpressed_index", columnList = "whenexpressed_id"),
	@Index(name = "expressionpattern_whereexpressed_index", columnList = "whereexpressed_id")
})
public class ExpressionPattern extends AuditedObject {

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class, CurationView.GeneExpressionDocument.class })
	private TemporalContext whenExpressed;

	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class })
	private AnatomicalSite whereExpressed;
}
