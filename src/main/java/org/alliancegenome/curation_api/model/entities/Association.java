package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.UniqueIdAuditedObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "association", description = "Annotation class representing a disease annotation")
@AGRCurationSchemaVersion(min="1.0.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"AuditedObject"})
public class Association extends UniqueIdAuditedObject {

}

