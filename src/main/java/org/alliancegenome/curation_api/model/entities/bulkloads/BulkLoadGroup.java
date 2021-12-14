package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
//@ToString(exclude = {"loads"})
public class BulkLoadGroup extends BaseGeneratedEntity {
    
    private String name;
    
    @OneToMany(mappedBy = "group")
    private List<BulkLoad> loads;
}
