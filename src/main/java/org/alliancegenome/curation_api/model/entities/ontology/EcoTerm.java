package org.alliancegenome.curation_api.model.entities.ontology;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import lombok.*;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

@Audited
@Indexed
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class EcoTerm extends OntologyTerm {
    
    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "abbreviation_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @JsonView(View.FieldsOnly.class)
    private String abbreviation;
    
}
