package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.alliancegenome.curation_api.base.entity.CurieAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Schema(name="Reference", description="POJO that represents the Reference")
public class Reference extends CurieAuditedObject {
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "displayXref_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(unique = true)
	@JsonView({View.FieldsOnly.class})
	@EqualsAndHashCode.Include
	private String displayXref;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "title_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	@EqualsAndHashCode.Include
	private String title;
	
	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = @Index( columnList = "reference_curie"))
	@JsonView({View.FieldsAndLists.class})
	private List<CrossReference> crossReferences;
	
}
