package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "annotation", description = "POJO that represents an annotation")
@AGRCurationSchemaVersion(min = "1.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "annotation_curie_index", columnList = "curie"),
		@Index(name = "annotation_uniqueId_index", columnList = "uniqueId"),
		@Index(name = "annotation_modEntityId_index", columnList = "modEntityId"),
		@Index(name = "annotation_modInternalId_index", columnList = "modInternalId"),
		@Index(name = "annotation_dataprovider_index", columnList = "dataProvider_id")
	}, uniqueConstraints = {
		@UniqueConstraint(name = "annotation_modentityid_uk", columnNames = "modEntityId"),
		@UniqueConstraint(name = "annotation_modinternalid_uk", columnNames = "modInternalId")
	}
)
public class Annotation extends SingleReferenceAssociation {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "uniqueId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(length = 3500)
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	protected String uniqueId;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "curie_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	protected String curie;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modEntityId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	private String modEntityId;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modInternalId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	@EqualsAndHashCode.Include
	private String modInternalId;

	@IndexedEmbedded(includePaths = {"handle", "uniqueId", "conditionRelationType.name", "singleReference.curie",
			"singleReference.crossReferences.referencedCurie", "conditions.conditionSummary", "conditions.uniqueId",
			"handle_keyword", "uniqueId_keyword", "conditionRelationType.name_keyword", "singleReference.curie_keyword",
			"singleReference.crossReferences.referencedCurie_keyword", "conditions.conditionSummary_keyword", "conditions.uniqueId_keyword" })
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.PhenotypeAnnotationView.class, View.ForPublic.class })
	@JoinTable(indexes = { @Index(name = "annotation_conditionrelation_annotation_index", columnList = "annotation_id"), @Index(name = "annotation_conditionrelation_conditionrelations_index", columnList = "conditionrelations_id")})
	private List<ConditionRelation> conditionRelations;

	@IndexedEmbedded(includePaths = {"freeText", "noteType.name", "references.curie", 
			"references.primaryCrossReferenceCurie", "freeText_keyword", "noteType.name_keyword", "references.curie_keyword", 
			"references.primaryCrossReferenceCurie_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.ForPublic.class })
	@JoinTable(indexes = { @Index(name = "annotation_note_annotation_index", columnList = "annotation_id"), @Index(name = "annotation_note_relatednotes_index", columnList = "relatednotes_id")})
	private List<Note> relatedNotes;

	@IndexedEmbedded(includePaths = {"sourceOrganization.abbreviation", "sourceOrganization.fullName", "sourceOrganization.shortName", "crossReference.displayName", "crossReference.referencedCurie",
			"sourceOrganization.abbreviation_keyword", "sourceOrganization.fullName_keyword", "sourceOrganization.shortName_keyword", "crossReference.displayName_keyword", "crossReference.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	protected DataProvider dataProvider;

}
