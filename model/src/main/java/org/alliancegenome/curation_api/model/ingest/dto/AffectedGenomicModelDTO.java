package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;

import lombok.Data;

@Data
@AGRCurationSchemaVersion(min="1.4.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={GenomicEntityDTO.class}, submitted=true)
public class AffectedGenomicModelDTO extends GenomicEntityDTO {

}
