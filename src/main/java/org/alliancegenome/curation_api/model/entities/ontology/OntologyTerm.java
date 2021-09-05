package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.*;

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
@ToString(exclude = {"parents", "children", "ancesters", "descendants", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets"}, callSuper = true)
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

    @ManyToMany
    @JoinTable(indexes = { @Index( columnList = "parents_curie"), @Index( columnList = "children_curie")})
    private Set<OntologyTerm> parents;

    @ManyToMany(mappedBy="parents")
    private Set<OntologyTerm> children;
    
    @ManyToMany
    @JoinTable(indexes = { @Index( columnList = "ancesters_curie"), @Index( columnList = "descendants_curie")})
    private Set<OntologyTerm> ancesters;

    @ManyToMany(mappedBy="ancesters")
    private Set<OntologyTerm> descendants;

    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @Column(columnDefinition="TEXT")
    @JsonView(View.FieldsOnly.class)
    private String definition;

    @ElementCollection
    @JsonView(View.FieldsOnly.class)
    private List<String> definitionUrls;

    @ElementCollection
    @JsonView(View.FieldsOnly.class)
    private List<String> subsets;

    @ElementCollection
    @JsonView(View.FieldsOnly.class)
    private List<String> secondaryIdentifiers;

    @ElementCollection
    @JsonView(View.FieldsOnly.class)
    private List<String> synonyms;

    @ManyToMany
    @JoinTable(indexes = @Index( columnList = "ontologyterm_curie"))
    @JsonView({View.FieldsOnly.class})
    private List<CrossReference> crossReferences;

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
    public void addAncester(OntologyTerm term) {
        if(ancesters == null) ancesters = new HashSet<OntologyTerm>();
        ancesters.add(term);
    }
}
