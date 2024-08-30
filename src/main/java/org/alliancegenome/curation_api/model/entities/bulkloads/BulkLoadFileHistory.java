package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "bulkLoadFile", "bulkLoad", "exceptions" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "bulkloadfilehistory_bulkLoadFile_index", columnList = "bulkLoadFile_id"),
		@Index(name = "bulkloadfilehistory_createdby_index", columnList = "createdBy_id"),
		@Index(name = "bulkloadfilehistory_updatedby_index", columnList = "updatedBy_id")
	}
)
public class BulkLoadFileHistory extends AuditedObject {

	@JsonView({ View.FieldsOnly.class })
	private LocalDateTime loadStarted = LocalDateTime.now();

	@JsonView({ View.FieldsOnly.class })
	private LocalDateTime loadFinished;
	
	@JsonView({ View.FieldsOnly.class })
	private Long totalRecords = 0L;

	@JsonView({ View.FieldsOnly.class })
	private Long failedRecords = 0L;

	@JsonView({ View.FieldsOnly.class })
	private Long completedRecords = 0L;

	@JsonView({ View.FieldsOnly.class })
	private Long totalDeleteRecords = 0L;

	@JsonView({ View.FieldsOnly.class })
	private Long deletedRecords = 0L;

	@JsonView({ View.FieldsOnly.class })
	private Long deleteFailedRecords = 0L;
	
	@JsonView({ View.FieldsOnly.class })
	private Double errorRate = 0.0;

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus bulkloadStatus = JobStatus.STOPPED;
	
	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String errorMessage;
	
	@ManyToOne
	@JsonView({ View.FieldsOnly.class })
	private BulkLoadFile bulkLoadFile;
	
	@ManyToOne
	private BulkLoad bulkLoad;

	@OneToMany(mappedBy = "bulkLoadFileHistory")
	@JsonView(View.BulkLoadFileHistoryView.class)
	private List<BulkLoadFileException> exceptions = new ArrayList<>();

	public BulkLoadFileHistory(long totalRecords) {
		this.totalRecords = totalRecords;
		loadStarted = LocalDateTime.now();
	}

	@Transient
	public void incrementCompleted() {
		if (errorRate > 0) {
			errorRate -= 1d / 1000d;
		}
		completedRecords++;
	}

	@Transient
	public void incrementFailed() {
		errorRate += 1d / 1000d;
		failedRecords++;
	}

	@Transient
	public void incrementDeleted() {
		deletedRecords++;
	}

	@Transient
	public void incrementDeleteFailed() {
		deleteFailedRecords++;
	}

	@Transient
	public void finishLoad() {
		loadFinished = LocalDateTime.now();
	}

}
