package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"Affected_Genomic_Model"})
public class AffectedGenomicModel extends GenomicEntity {

    
    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private Subtype subtype;
    
    @KeywordField(aggregable = Aggregable.YES, sortable = Sortable.YES, searchable = Searchable.YES)
    @JsonView({View.FieldsOnly.class})
    private String parental_population;

    //private List<AffectedGenomicModelComponent> components;
    //private List<SequenceTargetingReagent> sequence_targeting_reagents;
    
    public enum Subtype {
        strain, genotype;
    }
}

