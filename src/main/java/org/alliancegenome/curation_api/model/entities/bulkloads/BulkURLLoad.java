package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Entity;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name="BulkURLLoad", description="POJO that represents the BulkURLLoad")
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"BulkScheduledLoad"})
@JsonTypeName
public class BulkURLLoad extends BulkScheduledLoad {

	@JsonView({View.FieldsOnly.class})
	private String bulkloadUrl;

}
