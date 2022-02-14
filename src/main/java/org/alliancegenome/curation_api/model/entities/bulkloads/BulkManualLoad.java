package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;

import org.alliancegenome.curation_api.enums.BackendBulkDataType;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name="BulkManualLoad", description="POJO that represents the BulkManualLoad")
@JsonTypeName
public class BulkManualLoad extends BulkLoad {

    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private BackendBulkDataType dataType;
}
