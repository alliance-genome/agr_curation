package org.alliancegenome.curation_api.model.entities;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.CurieAuditedObject;
import org.hibernate.envers.Audited;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min="1.0.0", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
@Table(indexes = {
		@Index(name = "informationcontent_createdby_index", columnList = "createdBy_id"),
		@Index(name = "informationcontent_updatedby_index", columnList = "updatedBy_id"),
	})
public class InformationContentEntity extends CurieAuditedObject {

}

