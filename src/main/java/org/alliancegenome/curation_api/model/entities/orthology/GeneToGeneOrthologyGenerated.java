package org.alliancegenome.curation_api.model.entities.orthology;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.BooleanValueBridge;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "GeneToGeneOrthologyGenerated", description = "POJO that represents generated orthology between two genes")
@AGRCurationSchemaVersion(min = "1.7.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class, GeneToGeneOrthology.class })
@Table(indexes = {
	@Index(name = "genetogeneorthologygenerated_isbestscore_index", columnList = "isbestscore_id"),
	@Index(name = "genetogeneorthologygenerated_isbestscorereverse_index", columnList = "isbestscorereverse_id"),
	@Index(name = "genetogeneorthologygenerated_confidence_index", columnList = "confidence_id")
})
public class GeneToGeneOrthologyGenerated extends GeneToGeneOrthology {

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm isBestScore;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm isBestScoreReverse;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private VocabularyTerm confidence;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "strictFilter_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@Column(nullable = true)
	private Boolean strictFilter;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@KeywordField(name = "moderateFilter_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, valueBridge = @ValueBridgeRef(type = BooleanValueBridge.class))
	@JsonView({ View.FieldsOnly.class })
	@Column(nullable = true)
	private Boolean moderateFilter;
	
	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneorthologygenerated_predictionmethodsmatched", indexes = { @Index(name = "g2gorthgeneratedpmm_orthid_index", columnList = "genetogeneorthologygenerated_id"), @Index(name = "g2gorthgeneratedpmm_pmmid_index", columnList = "predictionmethodsmatched_id")})
	private List<VocabularyTerm> predictionMethodsMatched;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneorthologygenerated_predictionmethodsnotmatched", indexes = { @Index(name = "g2gorthgeneratedpmnm_orthid_index", columnList = "genetogeneorthologygenerated_id"), @Index(name = "g2gorthgeneratedpmnm_pmnmid_index", columnList = "predictionmethodsnotmatched_id")})
	private List<VocabularyTerm> predictionMethodsNotMatched;

	@IndexedEmbedded(includePaths = {"name", "name_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JsonView({ View.FieldsAndLists.class })
	@JoinTable(name = "genetogeneorthologygenerated_predictionmethodsnotcalled", indexes = { @Index(name = "g2gorthgeneratedpmnc_orthid_index", columnList = "genetogeneorthologygenerated_id"), @Index(name = "g2gorthgeneratedpmnc_pmncid_index", columnList = "predictionmethodsnotcalled_id")})
	private List<VocabularyTerm> predictionMethodsNotCalled;
}
