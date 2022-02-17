package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name="BulkURLLoad", description="POJO that represents the BulkURLLoad")
@JsonTypeName
public class BulkURLLoad extends BulkScheduledLoad {

    @JsonView({View.FieldsOnly.class})
    private String url;

}
