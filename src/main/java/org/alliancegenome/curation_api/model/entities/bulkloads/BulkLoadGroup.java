package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;

import lombok.Data;

@Data
@Entity
public class BulkLoadGroup extends BaseGeneratedEntity {
    
    @OneToMany(mappedBy = "group")
    private List<BulkLoad> loads;
}
