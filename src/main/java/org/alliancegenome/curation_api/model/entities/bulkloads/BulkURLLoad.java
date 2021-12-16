package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
public class BulkURLLoad extends BulkScheduledLoad {

    private String url;

}
