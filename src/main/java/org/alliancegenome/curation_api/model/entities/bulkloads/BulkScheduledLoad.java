package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public abstract class BulkScheduledLoad extends BulkLoad {

    private boolean scheduled;
    private String schedule;
}
