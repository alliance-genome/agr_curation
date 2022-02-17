package org.alliancegenome.curation_api.model.entities.bulkloads;


import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedEntity;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkiverse.hibernate.types.json.*;
import lombok.*;

@Audited
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoadFile"}, callSuper = true)
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
public class BulkLoadFileHistory extends BaseGeneratedEntity {

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
    private BulkLoadFile bulkLoadFile;
    
    @Type(type = JsonTypes.JSON_BIN)
    @JsonView({View.BulkLoadFileHistory.class})
    @Column(columnDefinition = JsonTypes.JSON_BIN)
    private List<ObjectUpdateExceptionData> exceptions = new ArrayList<>();
    
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
