package org.alliancegenome.curation_api.base.entity;

import java.time.OffsetDateTime;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.bridges.*;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkus.logging.Log;
import lombok.*;

@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
@ToString(exclude = {"createdBy", "modifiedBy"})
public class AuditedObject extends BaseEntity {


	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	private Person createdBy;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView(View.FieldsOnly.class)
	private Person modifiedBy;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({View.FieldsOnly.class})
	private OffsetDateTime dateCreated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	private OffsetDateTime dateUpdated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "internal_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean internal = false;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "obsolete_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	@Column(columnDefinition = "boolean default false", nullable = false)
	private Boolean obsolete = false;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dbDateCreated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView({View.FieldsOnly.class})
	@CreationTimestamp
	private OffsetDateTime dbDateCreated;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dbDateUpdated_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	@UpdateTimestamp
	private OffsetDateTime dbDateUpdated;
	
	
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	
	// Handle updating the dates
	// The idea here is if this is a new object and the dates are null these methods will
	// set the date to "now" before saving on which ever one or both if they are null
	
	// If this is an existing object then it HAS to be loaded in order to make an update on it.
	// In loading the object we save the dateUpdated to previousDateUpdated
	// 
	// if the caller does not set a New date then dateUpdated == previousDateUpdated and we
	// will force an updated date otherwise the caller did set the date and we will use that instead
	
	@PrePersist
	protected void onCreate() {
		//Log.info("onCreate: dateCreated: " + getClass().getSimpleName() + " " + dateCreated);
		//Log.info("onCreate: dateUpdated: " + getClass().getSimpleName() + " " + dateUpdated);
		OffsetDateTime date = OffsetDateTime.now();
		if(dateCreated == null) {
			dateCreated = date;
		}
		if(dateUpdated == null) {
			dateUpdated = date;
		}
	}
	
	@Transient
	private transient OffsetDateTime previousDateUpdated;
	
	@PostLoad
	protected void onPostLoad() {
		previousDateUpdated = dateUpdated;
	}

	@PreUpdate
	protected void onUpdate() {
		//Log.info("onUpdate: dateUpdated: " + getClass().getSimpleName() + " " + dateUpdated);
		//Log.info("onUpdate: previousDateUpdated: " + getClass().getSimpleName() + " " + previousDateUpdated);
		OffsetDateTime date = OffsetDateTime.now();
		
		if(dateCreated == null) {
			dateCreated = date;
		}
		if(dateUpdated == null || dateUpdated.equals(previousDateUpdated)) {
			dateUpdated = date;
		}
	}

}
