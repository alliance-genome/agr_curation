package org.alliancegenome.curation_api.model.entities.base;

import java.beans.Transient;
import java.io.Serializable;
import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.bridges.OffsetDateTimeValueBridge;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.view.CurationView;
import org.alliancegenome.curation_api.view.CurationView.VocabularyTermSetView;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = { "createdBy", "updatedBy" })
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
@MappedSuperclass
@Schema(name = "AuditedObject", description = "POJO that represents the AuditedObject")
public class AuditedObject implements Serializable {

	@Id
	@DocumentId
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({ CurationView.FieldsOnly.class, CurationView.PersonSettingView.class, VocabularyTermSetView.class })
	@EqualsAndHashCode.Include
	protected Long id;

	@IndexedEmbedded(includePaths = { "uniqueId", "uniqueId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView(CurationView.FieldsOnly.class)
	private Person createdBy;

	@IndexedEmbedded(includePaths = { "uniqueId", "uniqueId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView(CurationView.FieldsOnly.class)
	private Person updatedBy;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class })
	private OffsetDateTime dateCreated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(CurationView.FieldsOnly.class)
	private OffsetDateTime dateUpdated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "internal_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class })
	@Column(columnDefinition = "boolean default false")
	@ColumnDefault("false")
	private Boolean internal;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "obsolete_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ CurationView.FieldsOnly.class, CurationView.ForPublic.class })
	@Column(columnDefinition = "boolean default false")
	@ColumnDefault("false")
	private Boolean obsolete;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dbDateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({ CurationView.FieldsOnly.class })
	@CreationTimestamp
	private OffsetDateTime dbDateCreated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dbDateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(CurationView.FieldsOnly.class)
	@UpdateTimestamp
	private OffsetDateTime dbDateUpdated;

	@PrePersist
	@JsonIgnore
	public void defaultInternalObsolete() {
		if (internal == null) {
			internal = Boolean.FALSE;
		}
		if (obsolete == null) {
			obsolete = Boolean.FALSE;
		}
	}

	@Transient
	@JsonIgnore
	public boolean isNotInternalOrObsolete() {
		return !internal && !obsolete;
	}

}
