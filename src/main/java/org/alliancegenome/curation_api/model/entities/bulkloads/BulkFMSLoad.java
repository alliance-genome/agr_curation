package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
public class BulkFMSLoad extends BulkScheduledLoad {

    @JsonView({View.FieldsOnly.class})
    private String dataType;
    @JsonView({View.FieldsOnly.class})
    private String dataSubType;

}
