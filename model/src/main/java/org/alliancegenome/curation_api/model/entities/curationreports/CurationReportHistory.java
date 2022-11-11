package org.alliancegenome.curation_api.model.entities.curationreports;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"curationReport"}, callSuper = true)
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
public class CurationReportHistory extends GeneratedAuditedObject {

	@ManyToOne
	private CurationReport curationReport;

	@JsonView({View.FieldsOnly.class})
	private LocalDateTime curationReportTimestamp;

	@JsonView({View.FieldsAndLists.class})
	private String pdfFilePath;
	
	@JsonView({View.FieldsAndLists.class})
	private String xlsFilePath;
	
	@JsonView({View.FieldsAndLists.class})
	private String htmlFilePath;

	@JsonView({View.FieldsOnly.class})
	@Enumerated(EnumType.STRING)
	private JobStatus curationReportStatus;

	@Transient
	@JsonView({View.FieldsOnly.class})
	public String pdfUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + pdfFilePath;
	}
	
	@Transient
	@JsonView({View.FieldsOnly.class})
	public String xlsUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + xlsFilePath;
	}
	
	@Transient
	@JsonView({View.FieldsOnly.class})
	public String htmlUrl() {
		return "https://agr-curation-files.s3.amazonaws.com/" + htmlFilePath;
	}
	
}
