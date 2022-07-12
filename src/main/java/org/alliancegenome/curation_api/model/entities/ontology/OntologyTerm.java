package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.*;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(exclude = {/* "parents", "children", "ancesters", "descendants", */ "crossReferences", "synonyms", "secondaryIdentifiers", "subsets"}, callSuper = true)
public class OntologyTerm extends CurieAuditedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	@Column(length = 2000)
	private String name;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "type_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String type;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "namespace_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@JsonView(View.FieldsOnly.class)
	private String namespace;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@KeywordField(name = "definition_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
	@Column(columnDefinition="TEXT")
	@JsonView(View.FieldsOnly.class)
	private String definition;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@Column(columnDefinition="TEXT")
	@JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
	private List<String> definitionUrls;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
	private List<String> subsets;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
	private List<String> secondaryIdentifiers;

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
	@ElementCollection
	@JsonView(View.FieldsAndLists.class)
	@JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
	@Column(columnDefinition="TEXT")
	private List<String> synonyms;

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany
	@JoinTable(indexes = { @Index( columnList = "ontologyterm_curie"), @Index( columnList = "crossreferences_curie")})
	@JsonView({View.FieldsAndLists.class})
	private List<CrossReference> crossReferences;

	@ManyToMany
	@JoinTable(name = "ontologyterm_parent_children", indexes = { @Index( columnList = "parents_curie"), @Index( columnList = "children_curie")})
	private Set<OntologyTerm> parents;

	@ManyToMany(mappedBy = "parents")
	private Set<OntologyTerm> children;

	@ManyToMany
	@JoinTable(name = "ontologyterm_ancestor_descendant", indexes = { @Index( columnList = "ancestors_curie"), @Index( columnList = "descendants_curie")})
	private Set<OntologyTerm> ancestors;

	@ManyToMany(mappedBy = "ancestors")
	private Set<OntologyTerm> descendants;

	@Transient
	public void addChild(OntologyTerm term) {
		if(children == null) children = new HashSet<OntologyTerm>();
		children.add(term);
	}

	@Transient
	public void addParent(OntologyTerm term) {
		if(parents == null) parents = new HashSet<OntologyTerm>();
		parents.add(term);
	}

	@Transient
	public void addDescendant(OntologyTerm term) {
		if(descendants == null) descendants = new HashSet<OntologyTerm>();
		descendants.add(term);
	}

	@Transient
	public void addAncestor(OntologyTerm term) {
		if(ancestors == null) ancestors = new HashSet<OntologyTerm>();
		ancestors.add(term);
	}
	
}
