package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"synonyms", "crossReferences", "secondaryIdentifiers"})
public class GenomicEntity extends BiologicalEntity {

    //@Analyzer(definition = "caseInsensitiveAnalyzer")
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "name_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @Column(columnDefinition="TEXT")
    @JsonView({View.FieldsOnly.class})
    private String name;

    @ManyToMany
    @JoinTable(indexes = @Index( columnList = "genomicentities_curie"))
    @JsonView({View.FieldsAndLists.class})
    private List<Synonym> synonyms;

    @ManyToMany
    @JoinTable(indexes = @Index( columnList = "genomicentity_curie"))
    @JsonView({View.FieldsAndLists.class})
    private List<CrossReference> crossReferences;
    
    @ElementCollection
    @JoinTable(indexes = @Index( columnList = "genomicentity_curie"))
    @JsonView({View.FieldsAndLists.class})
    private List<String> secondaryIdentifiers;

}
