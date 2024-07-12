package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.base.SubmittedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "HighThroughputExpressionDatasetAnnotation", description = "POJO that represents the HighThroughputExpressionDatasetAnnotation")
public class HighThroughputExpressionDatasetAnnotation extends SubmittedObject{

	@JsonView({ View.FieldsOnly.class })
	private String name;   

    @IndexedEmbedded(includePaths = {"curie", "primaryCrossReferenceCurie", "crossReferences.referencedCurie", "curie_keyword", "primaryCrossReferenceCurie_keyword", "crossReferences.referencedCurie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@Fetch(FetchMode.JOIN)
	@JoinTable(indexes = {
		@Index(name = "htpdatasetannotation_reference_htpdataset_index", columnList = "highthroughputexpressiondatasetannotation_id"),
		@Index(name = "htpdatasetannotation_reference_references_index", columnList = "references_id")
	})
	@JsonView({ View.FieldsAndLists.class })
	private List<Reference> references;

    @JsonView({ View.FieldsOnly.class })
    private String realtedNote;

    @JsonView({ View.FieldsOnly.class })
    private Integer numberOfChannels;

    @IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @ManyToMany
    @JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "htpexpressiondatasetannotation_categorytags", indexes = { @Index(name = "htpdatasetannotation_htpdatasetid_index", columnList = "highthroughputexpressiondatasetannotation_id"), @Index(name = "htpdatasetannotation_categorytags_index", columnList = "categorytags_id")})
    List<VocabularyTerm> categoryTags;

}
