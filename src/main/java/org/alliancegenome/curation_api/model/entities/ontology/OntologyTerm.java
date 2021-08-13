package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"parent", "children", "crossReferences", "synonyms", "secondaryIdentifiers", "subsets", "definitionUrls"})
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
    
//  @ManyToOne
//  private OntologyTerm parent;
//  
//  @OneToMany
//  private List<OntologyTerm> children;
    
    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @Column(columnDefinition="TEXT")
    @JsonView(View.FieldsOnly.class)
    private String definition;
    
//  @ElementCollection
//  private List<String> definitionUrls;
//
//  @ElementCollection
//  private List<String> subsets;
//
//  @ElementCollection
//  private List<String> secondaryIdentifiers;
//
//  @ManyToMany
//  private List<Synonym> synonyms;
//  
//  @ManyToMany
//  private List<CrossReference> crossReferences;

}
