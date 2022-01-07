package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"genomicLocations"})
@Schema(name="Gene", description="POJO that represents the Gene")
public class Gene extends GenomicEntity {

    @FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer")
    @KeywordField(name = "symbol_keyword", aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES, normalizer = "sortNormalizer")
    @JsonView({View.FieldsOnly.class})
    private String symbol;

    @FullTextField
    @Column(columnDefinition="TEXT")
    @JsonView({View.FieldsOnly.class})
    private String geneSynopsis;

    @KeywordField
    @JsonView({View.FieldsOnly.class})
    private String geneSynopsisURL;

    @ManyToOne
    @JsonView({View.FieldsOnly.class})
    private SOTerm geneType;
    
    @FullTextField
    @Column(columnDefinition="TEXT")
    @JsonView({View.FieldsOnly.class})
    private String automatedGeneDescription;

    @ManyToMany
    private List<GeneGenomicLocation> genomicLocations;

}

