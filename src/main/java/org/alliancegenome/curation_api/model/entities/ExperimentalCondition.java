package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.UniqueIdAuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Indexed
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ExperimentalCondition", description = "POJO that describes the Experimental Condition")
@AGRCurationSchemaVersion("1.2.1")
public class ExperimentalCondition extends UniqueIdAuditedObject {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private ZECOTerm conditionClass;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "conditionSummary_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	private String conditionSummary;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "conditionStatement_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	private String conditionStatement;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private ExperimentalConditionOntologyTerm conditionId;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "conditionQuantity_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	private String conditionQuantity;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private AnatomicalTerm conditionAnatomy;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private GOTerm conditionGeneOntology;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private NCBITaxonTerm conditionTaxon;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({View.FieldsOnly.class})
	private ChemicalTerm conditionChemical;
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "conditionFreeText_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition="TEXT")
	private String conditionFreeText;
}
