package org.alliancegenome.curation_api.model.entities.bulkloads;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.13.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BulkScheduledLoad.class })
@Schema(name = "BulkGoogleAnalyticsLoad", description = "POJO that represents the BulkLoadGoogleAnalyticsLoad")
@JsonTypeName
public class BulkGoogleAnalyticsLoad extends BulkScheduledLoad {
	
}
