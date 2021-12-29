package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@Schema(name="BulkManualLoad", description="POJO that represents the BulkManualLoad")
@JsonTypeName
public class BulkManualLoad extends BulkLoad {

}
