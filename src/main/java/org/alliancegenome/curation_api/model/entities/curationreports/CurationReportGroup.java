package org.alliancegenome.curation_api.model.entities.curationreports;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"AuditedObject"})
public class CurationReportGroup extends GeneratedAuditedObject {
	
	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@JsonView({View.FieldsAndLists.class})
	@OneToMany(mappedBy = "curationReportGroup")
	private List<CurationReport> curationReports;
}
