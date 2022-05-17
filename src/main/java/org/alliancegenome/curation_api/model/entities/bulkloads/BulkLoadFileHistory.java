package org.alliancegenome.curation_api.model.entities.bulkloads;


import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoadFile", "exceptions"}, callSuper = true)
public class BulkLoadFileHistory extends GeneratedAuditedObject {

    @JsonView({View.FieldsOnly.class})
    private LocalDateTime loadStarted;
    
    @JsonView({View.FieldsOnly.class})
    private LocalDateTime loadFinished;
    
    @JsonView({View.FieldsOnly.class})
    private Long totalRecords = 0l;
    
    @JsonView({View.FieldsOnly.class})
    private Long failedRecords = 0l;
    
    @JsonView({View.FieldsOnly.class})
    private Long completedRecords = 0l;
    
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private BulkLoadFile bulkLoadFile;

    @JsonView(View.BulkLoadFileHistory.class)
    @OneToMany(mappedBy = "bulkLoadFileHistory")
    private List<BulkLoadFileException> exceptions = new ArrayList<>();
    
    public BulkLoadFileHistory(long totalRecords) {
        this.totalRecords = totalRecords;
        loadStarted = LocalDateTime.now();
    }

    @Transient
    public void incrementCompleted() {
        completedRecords++;
    }
    @Transient
    public void incrementFailed() {
        failedRecords++;
    }
    
    @Transient
    public void finishLoad() {
        loadFinished = LocalDateTime.now();
    }

}
