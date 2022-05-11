package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.GeneratedAuditedObject;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class GeneGenomicLocation extends GeneratedAuditedObject {
    
    //private Subject subject;
    //private Predicate predicate;
    //private AGRObject object;
    private String assembly;
    private Integer startPos;
    private Integer endPos;
    
}
