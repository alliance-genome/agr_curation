package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.jobs.BulkLoadFileProcessor;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"group"})
public abstract class BulkLoad extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private String name;
    
    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private BulkLoadStatus status;
    
    @ManyToOne
    private BulkLoadGroup group;
    
    @JsonView({View.FieldsOnly.class})
    @OneToMany(mappedBy = "bulkLoad", fetch = FetchType.EAGER)
    private List<BulkLoadFile> loadFiles;
    
    public enum BulkLoadStatus {
        STARTED,
        RUNNING,
        STOPPED,
        FINISHED,
        PENDING,
        FAILED,
        DOWNLOADING,
        ADMINISTRATIVELY_STOPPED,
        PAUSED;
    }

}
