package org.alliancegenome.curation_api.model.entities.base;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Note;
import org.alliancegenome.curation_api.model.entities.Organization;
import org.alliancegenome.curation_api.view.View;
import org.apache.commons.lang3.StringUtils;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.9.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {CurieObject.class})
@Schema(name = "SubmittedObject", description = "POJO that represents the SubmittedObject")
public class SubmittedObject extends CurieObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "primaryExternalId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class, View.ForPublic.class, View.GeneToGeneOrthologyDocument.class, View.GeneSummaryDocument.class, View.ModelDocumentView.class, View.TransgenicAllelesDocumentView.class, View.AlleleSummaryDocument.class, View.GeneExpressionDocument.class })
	private String primaryExternalId;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "modInternalId_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	private String modInternalId;

	@IndexedEmbedded(includePaths = {
			"abbreviation", "fullName", "shortName",
			"abbreviation_keyword", "fullName_keyword", "shortName_keyword"
	})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({View.FieldsOnly.class, View.GeneSummaryDocument.class, View.ModelDocumentView.class})
	private Organization dataProvider;

	@IndexedEmbedded(includePaths = {"displayName", "referencedCurie", "displayName_keyword", "referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne(orphanRemoval = true)
	@Fetch(FetchMode.SELECT)
	@JsonView({View.FieldsOnly.class, View.AlleleSummaryDocument.class, View.AlleleForPublic.class, View.TransgenicAllelesDocumentView.class})
	private CrossReference dataProviderCrossReference;

	@IndexedEmbedded(includePaths = {"freeText", "freeText_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView({ View.FieldsAndLists.class, View.AlleleView.class, View.AlleleDetailView.class, View.GeneView.class, View.AffectedGenomicModelView.class, View.ConstructView.class, View.VariantView.class })
	@JoinTable(
		joinColumns = @JoinColumn(name = "submittedobject_id"),
		inverseJoinColumns = @JoinColumn(name = "relatednotes_id"),
		indexes = {
			@Index(columnList = "submittedobject_id"),
			@Index(columnList = "relatednotes_id")
		}
	)
	private List<Note> relatedNotes;

	@Transient
	@JsonIgnore
	public String getIdentifier() {
		if (StringUtils.isNotBlank(curie)) {
			return curie;
		}
		if (StringUtils.isNotBlank(primaryExternalId)) {
			return primaryExternalId;
		}
		if (StringUtils.isNotBlank(modInternalId)) {
			return modInternalId;
		}
		return null;
	}

	@Transient
	@JsonIgnore
	public String getSubmittedIdentifier() {
		if (StringUtils.isNotBlank(primaryExternalId)) {
			return primaryExternalId;
		}
		if (StringUtils.isNotBlank(modInternalId)) {
			return modInternalId;
		}
		return null;
	}
}
