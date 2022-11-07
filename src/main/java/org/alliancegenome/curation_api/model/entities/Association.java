package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.hibernate.envers.Audited;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@MappedSuperclass
@AGRCurationSchemaVersion(min="1.0.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
public class Association extends GeneratedAuditedObject {

}

