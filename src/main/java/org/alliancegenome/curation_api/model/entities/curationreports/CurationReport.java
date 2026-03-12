package org.alliancegenome.curation_api.model.entities.curationreports;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "CurationReport", description = "CurationReport: a curation report")
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@ToString(exclude = { "curationReportGroup", "curationReportHistory" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class CurationReport extends AuditedObject {

	@JsonView({ CurationView.FieldsOnly.class })
	private String name;

	@JsonView({ CurationView.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus curationReportStatus;

	@JsonView({ CurationView.FieldsOnly.class })
	private String cronSchedule;

	@JsonView({ CurationView.FieldsOnly.class })
	private Boolean scheduleActive;

	@JsonView({ CurationView.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String schedulingErrorMessage;

	@JsonView({ CurationView.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@ManyToOne
	private CurationReportGroup curationReportGroup;

	@JsonView({ CurationView.FieldsOnly.class })
	private String birtReportFilePath;

	@JsonView({ CurationView.ReportHistory.class })
	@OneToMany(mappedBy = "curationReport")
	@OrderBy("curationReportTimestamp DESC")
	private List<CurationReportHistory> curationReportHistory;

}
