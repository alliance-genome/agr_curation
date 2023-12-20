package org.alliancegenome.curation_api.model.entities.base;

import java.time.OffsetDateTime;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.bridges.OffsetDateTimeValueBridge;
import org.alliancegenome.curation_api.model.entities.Association;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.DataProvider;
import org.alliancegenome.curation_api.model.entities.ExperimentalCondition;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.model.entities.Person;
import org.alliancegenome.curation_api.model.entities.PersonSetting;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptor;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.Synonym;
import org.alliancegenome.curation_api.model.entities.Vocabulary;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.VocabularyTermSet;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFile;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileException;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadFileHistory;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoadGroup;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthology;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.SlotAnnotation;
import org.alliancegenome.curation_api.view.View;
import org.alliancegenome.curation_api.view.View.VocabularyTermSetView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = Association.class, name = "Association"),
	@JsonSubTypes.Type(value = BulkLoad.class, name = "BulkLoad"),
	@JsonSubTypes.Type(value = BulkLoadFile.class, name = "BulkLoadFile"),
	@JsonSubTypes.Type(value = BulkLoadFileException.class, name = "BulkLoadFileException"),
	@JsonSubTypes.Type(value = BulkLoadFileHistory.class, name = "BulkLoadFileHistory"),
	@JsonSubTypes.Type(value = BulkLoadGroup.class, name = "BulkLoadGroup"),
	@JsonSubTypes.Type(value = CrossReference.class, name = "CrossReference"),
	@JsonSubTypes.Type(value = CurieObject.class, name = "CurieObject"),
	@JsonSubTypes.Type(value = DataProvider.class, name = "DataProvider"),
	@JsonSubTypes.Type(value = GeneToGeneOrthology.class, name = "GeneToGeneOrthology"),
	@JsonSubTypes.Type(value = Note.class, name = "Note"),
	@JsonSubTypes.Type(value = Organization.class, name = "Organization"),
	@JsonSubTypes.Type(value = Person.class, name = "Person"),
	@JsonSubTypes.Type(value = PersonSetting.class, name = "PersonSetting"),
	@JsonSubTypes.Type(value = ResourceDescriptor.class, name = "ResourceDescriptor"),
	@JsonSubTypes.Type(value = ResourceDescriptorPage.class, name = "ResourceDescriptorPage"),
	@JsonSubTypes.Type(value = SlotAnnotation.class, name = "SlotAnnotation"),
	@JsonSubTypes.Type(value = Species.class, name = "Species"),
	@JsonSubTypes.Type(value = Synonym.class, name = "Synonym"),
	@JsonSubTypes.Type(value = UniqueIdAuditedObject.class, name = "UniqueIdAuditedObject"),
	@JsonSubTypes.Type(value = Vocabulary.class, name = "Vocabulary"),
	@JsonSubTypes.Type(value = VocabularyTerm.class, name = "VocabularyTerm"),
	@JsonSubTypes.Type(value = VocabularyTermSet.class, name = "VocabularyTermSet"), 
})
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Audited
@Entity
@ToString(exclude = { "createdBy", "updatedBy" })
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(indexes = {
		@Index(name = "auditedobject_createdby_index", columnList = "createdBy_id"),
		@Index(name = "auditedobject_updatedby_index", columnList = "updatedBy_id")
})
public class AuditedObject extends BaseEntity {

	@Id
	@DocumentId
	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@JsonView({ View.FieldsOnly.class, View.PersonSettingView.class, VocabularyTermSetView.class })
	@EqualsAndHashCode.Include
	protected Long id;
	
	@IndexedEmbedded(includePaths = {"uniqueId", "uniqueId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView(View.FieldsOnly.class)
	private Person createdBy;

	@IndexedEmbedded(includePaths = {"uniqueId", "uniqueId_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView(View.FieldsOnly.class)
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
