package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoad"})
public class BulkLoadFile extends BaseGeneratedEntity {

    @Column(unique=true)
    private String md5Sum;
    private String s3Path;
    
    @ManyToOne
    private BulkLoad bulkLoad;
    
    @Transient
    private String getS3Url() {
        // TODO craft proper URL based on system
        return s3Path;
    }
}
