package org.alliancegenome.curation_api.model.entities;

import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.UniqueIdAuditedObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min="1.2.1", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"AuditedObject"})
@MappedSuperclass
public class Agent extends UniqueIdAuditedObject {

}
