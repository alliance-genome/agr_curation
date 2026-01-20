package org.alliancegenome.curation_api.model.entities.curationreports;

import java.time.LocalDateTime;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "curationReport" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class CurationReportHistory extends AuditedObject {

	@ManyToOne
	private CurationReport curationReport;

	@JsonView({ CurationView.FieldsOnly.class })
	private LocalDateTime curationReportTimestamp;

	@JsonView({ CurationView.FieldsAndLists.class })
	private String pdfFilePath;

	@JsonView({ CurationView.FieldsAndLists.class })
	private String xlsFilePath;

	@JsonView({ CurationView.FieldsAndLists.class })
	private String htmlFilePath;

	@JsonView({ CurationView.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus curationReportStatus;

	@Transient
	@JsonView({ CurationView.FieldsOnly.class })
	public String pdfUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + pdfFilePath;
	}

	@Transient
	@JsonView({ CurationView.FieldsOnly.class })
	public String xlsUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + xlsFilePath;
	}

	@Transient
	@JsonView({ CurationView.FieldsOnly.class })
	public String htmlUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + htmlFilePath;
	}

}
