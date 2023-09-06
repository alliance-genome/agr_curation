package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Schema(name = "association", description = "POJO that represents an association")
@AGRCurationSchemaVersion(min = "1.3.3", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })

@Table(indexes = { 
	@Index(name = "association_createdby_index", columnList = "createdBy_id"), 
	@Index(name = "association_updatedby_index", columnList = "updatedBy_id"),
})

public class Association extends GeneratedAuditedObject {
	
}
