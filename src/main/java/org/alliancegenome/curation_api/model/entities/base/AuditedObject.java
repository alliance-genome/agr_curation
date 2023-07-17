package org.alliancegenome.curation_api.model.entities.base;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.bridges.OffsetDateTimeValueBridge;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
@ToString(exclude = { "createdBy", "updatedBy" })
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
public class AuditedObject extends BaseEntity {

	@IndexedEmbedded(includePaths = {"uniqueId"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	@Fetch(FetchMode.JOIN)
	private Person createdBy;

	@IndexedEmbedded(includePaths = {"uniqueId"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	@Fetch(FetchMode.JOIN)
	private Person updatedBy;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	private OffsetDateTime dateCreated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	private OffsetDateTime dateUpdated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "internal_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean internal = false;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "obsolete_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean obsolete = false;

	//@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	//@KeywordField(name = "dbDateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@CreationTimestamp
	private OffsetDateTime dbDateCreated;

	//@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	//@KeywordField(name = "dbDateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	@UpdateTimestamp
	private OffsetDateTime dbDateUpdated;
}
