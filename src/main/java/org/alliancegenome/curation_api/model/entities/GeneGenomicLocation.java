package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
public class GeneGenomicLocation extends BaseGeneratedEntity {
    
    //private Subject subject;
    //private Predicate predicate;
    //private AGRObject object;
    private String assembly;
    private Integer startPos;
    private Integer endPos;
    
}
