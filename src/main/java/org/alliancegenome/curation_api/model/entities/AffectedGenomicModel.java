package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import lombok.*;

@Audited
@Indexed
@Entity
@Data @EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"Affected_Genomic_Model"})
public class AffectedGenomicModel extends GenomicEntity {

    
    @Enumerated(EnumType.STRING)
    private Subtype subtype;
    
    
    //private List<AffectedGenomicModelComponent> components;
    //private List<SequenceTargetingReagent> sequence_targeting_reagents;
    
    
    private String parental_population;

    public enum Subtype {
        strain, genotype;
    }
}

