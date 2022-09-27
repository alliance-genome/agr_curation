package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE)
@Schema(name="BulkFMSLoad", description="POJO that represents the BulkFMSLoad")
@JsonTypeName
public class BulkFMSLoad extends BulkScheduledLoad {

	@JsonView({View.FieldsOnly.class})
	private String fmsDataType;
	@JsonView({View.FieldsOnly.class})
	private String fmsDataSubType;

}
