package org.alliancegenome.curation_api.model.entities.curationreports;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"curationReportGroup", "curationReportHistory"}, callSuper = true)
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={"AuditedObject"})
public class CurationReport extends GeneratedAuditedObject {

	@JsonView({View.FieldsOnly.class})
	private String name;

	@JsonView({View.FieldsOnly.class})
	@Enumerated(EnumType.STRING)
	private JobStatus curationReportStatus;

	@JsonView({View.FieldsOnly.class})
	private String cronSchedule;
	
	@JsonView({View.FieldsOnly.class})
	private Boolean scheduleActive;
	
	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition="TEXT")
	private String schedulingErrorMessage;
	
	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition="TEXT")
	private String errorMessage;
	
	@ManyToOne
	private CurationReportGroup curationReportGroup;

	@JsonView({View.FieldsOnly.class})
	private String birtReportFilePath;
	
	@JsonView({View.ReportHistory.class})
	@OneToMany(mappedBy = "curationReport", fetch = FetchType.EAGER)
	@OrderBy("curationReportTimestamp DESC")
	private List<CurationReportHistory> curationReportHistory;
	
}
