package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.*;

import javax.persistence.*;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(exclude = { "isaParents", "isaChildren", "isaAncestors", "isaDescendants", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets"}, callSuper = true)
@AGRCurationSchemaVersion("1.2.1")
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
	//@JsonView(View.OntologyTermView.class)
	@JoinTable(name = "ontologyterm_isa_parent_children", indexes = { @Index( columnList = "isaparents_curie"), @Index( columnList = "isachildren_curie")})
	private Set<OntologyTerm> isaParents;

	@ManyToMany(mappedBy = "isaParents")
	//@JsonView(View.OntologyTermView.class)
	private Set<OntologyTerm> isaChildren;

	@ManyToMany
	@JoinTable(name = "ontologyterm_isa_ancestor_descendant", indexes = { @Index( columnList = "isaancestors_curie"), @Index( columnList = "isadescendants_curie")})
	private Set<OntologyTerm> isaAncestors;

	@ManyToMany(mappedBy = "isaAncestors")
	private Set<OntologyTerm> isaDescendants;

	@Transient
	public void addIsaChild(OntologyTerm term) {
		if(isaChildren == null) isaChildren = new HashSet<OntologyTerm>();
		isaChildren.add(term);
	}

	@Transient
	public void addIsaParent(OntologyTerm term) {
		if(isaParents == null) isaParents = new HashSet<OntologyTerm>();
		isaParents.add(term);
	}

	@Transient
	public void addIsaDescendant(OntologyTerm term) {
		if(isaDescendants == null) isaDescendants = new HashSet<OntologyTerm>();
		isaDescendants.add(term);
	}

	@Transient
	public void addIsaAncestor(OntologyTerm term) {
		if(isaAncestors == null) isaAncestors = new HashSet<OntologyTerm>();
		isaAncestors.add(term);
	}
	
}
