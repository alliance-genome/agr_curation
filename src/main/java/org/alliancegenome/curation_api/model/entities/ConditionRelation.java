package org.alliancegenome.curation_api.model.entities;

import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.UniqueIdAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Indexed
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "ConditionRelation", description = "POJO that describes the Condition Relation")
@Table(indexes = { 
		@Index(name = "conditionrelation_createdby_index", columnList = "createdBy_id"), 
		@Index(name = "conditionrelation_updatedby_index", columnList = "updatedBy_id")
})
@AGRCurationSchemaVersion(min = "1.2.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { ExperimentalCondition.class })
public class ConditionRelation extends UniqueIdAuditedObject {


	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class, View.ForPublic.class })
	private VocabularyTerm conditionRelationType;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "handle_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String handle;

	@IndexedEmbedded(includeDepth = 2)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@Fetch(FetchMode.SELECT)
	@JsonView({ View.FieldsOnly.class })
	private Reference singleReference;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class, View.DiseaseAnnotation.class, View.PhenotypeAnnotationView.class, View.ConditionRelationView.class, View.ForPublic.class })
	private List<ExperimentalCondition> conditions;

	public void addExperimentCondition(ExperimentalCondition experimentalCondition) {
		if (conditions == null) {
			conditions = new ArrayList<>();
		}
		conditions.add(experimentalCondition);
	}

	public static class Constant {
		public static final String HANDLE_STANDARD = "Standard";
		public static final String HANDLE_GENERIC_CONTROL = "Generic_control";
	}
}