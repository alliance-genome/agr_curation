package org.alliancegenome.curation_api.model.entities.bulkloads;


import java.util.*;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedEntity;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
public class BulkLoadFileHistory extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private Long totalRecords;
    
    @JsonView({View.FieldsOnly.class})
    private Long failedRecords;
    
    @JsonView({View.FieldsOnly.class})
    private Long completedRecords;
    
    @ManyToOne
    private BulkLoadFile bulkLoadFile;
    
    @Transient
    private List<ObjectUpdateException> exceptions = new ArrayList<>();
    
    
    
    public BulkLoadFileHistory(long totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Transient
    public void incrementCompleted() {
        completedRecords++;
    }
    @Transient
    public void incrementFailed() {
        failedRecords++;
    }

}
