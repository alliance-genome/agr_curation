package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public abstract class BulkScheduledLoad extends BulkLoad {

    @JsonView({View.FieldsOnly.class})
    private boolean scheduled;
    @JsonView({View.FieldsOnly.class})
    private String schedule;
}
