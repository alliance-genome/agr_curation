package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"group"})
public abstract class BulkLoad extends BaseGeneratedEntity {

    @ManyToOne
    private BulkLoadGroup group;
    
    @OneToMany(mappedBy = "bulkLoad")
    private List<BulkLoadFile> loadFiles;

}
