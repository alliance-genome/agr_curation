package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class BulkFMSLoad extends BulkScheduledLoad {

    private String dataType;
    private String dataSubType;

}
