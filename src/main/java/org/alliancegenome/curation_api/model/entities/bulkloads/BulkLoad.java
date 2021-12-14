package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;

import lombok.Data;

@Data
@Entity
public abstract class BulkLoad extends BaseGeneratedEntity {

    @ManyToOne
    private BulkLoadGroup group;
    
    @OneToMany(mappedBy = "bulkLoad")
    private List<BulkLoadFile> loadFiles;
    
    private boolean scheduled;
    private String schedule;
}
