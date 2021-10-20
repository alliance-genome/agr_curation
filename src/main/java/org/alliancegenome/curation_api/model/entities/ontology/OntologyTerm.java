package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(exclude = {/* "parents", "children", "ancesters", "descendants", */ "crossReferences", "synonyms", "secondaryIdentifiers", "subsets"}, callSuper = true)
public class OntologyTerm extends BaseCurieEntity {

    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView(View.FieldsOnly.class)
    private String name;

    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView(View.FieldsOnly.class)
    private String type;

    @GenericField
    @JsonView(View.FieldsOnly.class)
    private Boolean obsolete;

    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView(View.FieldsOnly.class)
    private String namespace;

    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @Column(columnDefinition="TEXT")
    @JsonView(View.FieldsOnly.class)
    private String definition;

    @ElementCollection
    @JsonView(View.FieldsAndLists.class)
    @Column(columnDefinition="TEXT")
    @JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
    private List<String> definitionUrls;

    @ElementCollection
    @JsonView(View.FieldsAndLists.class)
    @JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
    private List<String> subsets;

    @ElementCollection
    @JsonView(View.FieldsAndLists.class)
    @JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
    private List<String> secondaryIdentifiers;

    @ElementCollection
    @JsonView(View.FieldsAndLists.class)
    @JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
    @Column(columnDefinition="TEXT")
    private List<String> synonyms;

    @ManyToMany
    @JoinTable(indexes = { @Index( columnList = "ontologyterm_curie"), @Index( columnList = "crossreferences_curie")})
    @JsonView({View.FieldsAndLists.class})
    private List<CrossReference> crossReferences;

//  TODO LinkML to define the following fields
//  @ManyToMany
//  @JoinTable(name = "ontologyterm_parent_children", indexes = { @Index( columnList = "parents_curie"), @Index( columnList = "children_curie")})
//  private Set<OntologyTerm> parents;
//
//  @ManyToMany(mappedBy = "parents")
//  private Set<OntologyTerm> children;
//
//  @ManyToMany
//  @JoinTable(name = "ontologyterm_ancestor_descendant", indexes = { @Index( columnList = "ancestors_curie"), @Index( columnList = "descendants_curie")})
//  private Set<OntologyTerm> ancestors;
//
//  @ManyToMany(mappedBy = "ancestors")
//  private Set<OntologyTerm> descendants;
//
//  @Transient
//  public void addChild(OntologyTerm term) {
//      if(children == null) children = new HashSet<OntologyTerm>();
//      children.add(term);
//  }
//
//  @Transient
//  public void addParent(OntologyTerm term) {
//      if(parents == null) parents = new HashSet<OntologyTerm>();
//      parents.add(term);
//  }
//
//  @Transient
//  public void addDescendant(OntologyTerm term) {
//      if(descendants == null) descendants = new HashSet<OntologyTerm>();
//      descendants.add(term);
//  }
//
//  @Transient
//  public void addAncestor(OntologyTerm term) {
//      if(ancestors == null) ancestors = new HashSet<OntologyTerm>();
//      ancestors.add(term);
//  }
    
}
