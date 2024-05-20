package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.BulkLoadCleanUp;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.OffsetDateTimeValueBridge;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "bulkLoad" })
@AGRCurationSchemaVersion(min = "1.3.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "bulkloadfile_bulkLoad_index", columnList = "bulkLoad_id"),
		@Index(name = "bulkloadfile_createdby_index", columnList = "createdBy_id"),
		@Index(name = "bulkloadfile_updatedby_index", columnList = "updatedBy_id"),
		@Index(name = "bulkloadfile_bulkloadStatus_index", columnList = "bulkloadStatus"),
	}
)
public class BulkLoadFile extends AuditedObject {

	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateLastLoaded_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	private OffsetDateTime dateLastLoaded;

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus bulkloadStatus;
	
	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private BulkLoadCleanUp bulkloadCleanUp;

	@JsonView({ View.FieldsOnly.class })
	@Column(unique = true)
	private String md5Sum;

	@JsonView({ View.FieldsOnly.class })
	private String localFilePath;

	@JsonView({ View.FieldsOnly.class })
	private Long fileSize;

	@JsonView({ View.FieldsOnly.class })
	private String s3Path;

	@JsonView({ View.FieldsOnly.class })
	private Integer recordCount;

	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@JsonView({ View.FieldsOnly.class })
	private String linkMLSchemaVersion;
	
	@JsonView({ View.FieldsOnly.class })
	private String allianceMemberReleaseVersion;

	@ManyToOne
	private BulkLoad bulkLoad;

	@JsonView({ View.FieldsOnly.class })
	@OneToMany(mappedBy = "bulkLoadFile", fetch = FetchType.EAGER)
	@OrderBy("loadFinished DESC")
	private List<BulkLoadFileHistory> history;

	@Transient
	@JsonView({ View.FieldsOnly.class })
	public String getS3Url() {
		return "https://agr-curation-files.s3.amazonaws.com/" + s3Path;
	}

	@Transient
	@JsonIgnore
	@JsonView({ View.FieldsOnly.class })
	public String generateS3MD5Path() {
		if (md5Sum != null && md5Sum.length() > 0) {
			return md5Sum.charAt(0) + "/" + md5Sum.charAt(1) + "/" + md5Sum.charAt(2) + "/" + md5Sum.charAt(3) + "/" + md5Sum + "." + bulkLoad.getBackendBulkLoadType().fileExtension + ".gz";
		} else {
			return null;
		}
	}
}
