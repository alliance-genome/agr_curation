package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.model.entities.ontology.NCBITaxonTerm;
import org.alliancegenome.curation_api.view.View;
import org.apache.lucene.document.FieldType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Audited
@Entity
@Data
@Indexed
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE,dependencies = { AuditedObject.class })
@Table(indexes = { @Index(name = "species_createdby_index", columnList = "createdBy_id"), @Index(name = "species_updatedby_index", columnList = "updatedBy_id") })
public class Species extends GeneratedAuditedObject {

	@IndexedEmbedded(includePaths = {"name", "curie", "name_keyword", "curie_keyword"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	@Fetch(FetchMode.JOIN)
	private NCBITaxonTerm taxon;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "fullName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String fullName;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "shortName_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String shortName;

	@KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@ElementCollection
	@JsonView({ View.FieldsAndLists.class})
	@JoinTable(indexes = @Index(name ="species_commonnames_species_id_index",columnList = "species_id"))
	private List<String> commonNames;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@OneToOne
	@JsonView({ View.FieldsOnly.class })
	private Organization sourceOrganization;

	@GenericField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
	@JsonView(View.FieldsOnly.class)
	private Integer phylogenicOrder;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "assembly_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView({ View.FieldsOnly.class })
	private String assembly;
}
