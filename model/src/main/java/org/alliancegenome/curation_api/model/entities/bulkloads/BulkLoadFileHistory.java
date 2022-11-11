package org.alliancegenome.curation_api.model.entities.bulkloads;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Audited
@Entity
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoadFile", "exceptions"}, callSuper = true)
@AGRCurationSchemaVersion(min="1.2.4", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
public class BulkLoadFileHistory extends GeneratedAuditedObject {

	@JsonView({View.FieldsOnly.class})
	private LocalDateTime loadStarted;
	
	@JsonView({View.FieldsOnly.class})
	private LocalDateTime loadFinished;
	
	@JsonView({View.FieldsOnly.class})
	private Long totalRecords = 0l;
	
	@JsonView({View.FieldsOnly.class})
	private Long failedRecords = 0l;
	
	@JsonView({View.FieldsOnly.class})
	private Long completedRecords = 0l;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BulkLoadFile bulkLoadFile;

	@JsonView(View.BulkLoadFileHistory.class)
	@OneToMany(mappedBy = "bulkLoadFileHistory")
	private List<BulkLoadFileException> exceptions = new ArrayList<>();
	
	public BulkLoadFileHistory(long totalRecords) {
		this.totalRecords = totalRecords;
		loadStarted = LocalDateTime.now();
	}

	@Transient
	public void incrementCompleted() {
		completedRecords++;
	}
	@Transient
	public void incrementFailed() {
		failedRecords++;
	}
	
	@Transient
	public void finishLoad() {
		loadFinished = LocalDateTime.now();
	}

}
