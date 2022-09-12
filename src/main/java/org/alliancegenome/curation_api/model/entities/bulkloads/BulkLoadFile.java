package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.time.OffsetDateTime;
import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.bridges.OffsetDateTimeValueBridge;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoad"})
@AGRCurationSchemaVersion("1.2.4")
public class BulkLoadFile extends GeneratedAuditedObject {
	
	@FullTextField(analyzer = "autocompleteAnalyzer", searchAnalyzer = "autocompleteSearchAnalyzer", valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@KeywordField(name = "dateLastLoaded_keyword", sortable = Sortable.YES, searchable = Searchable.YES, aggregable = Aggregable.YES, valueBridge = @ValueBridgeRef(type = OffsetDateTimeValueBridge.class))
	@JsonView(View.FieldsOnly.class)
	private OffsetDateTime dateLastLoaded;
	
	@JsonView({View.FieldsOnly.class})
	@Enumerated(EnumType.STRING)
	private JobStatus bulkloadStatus;

	@JsonView({View.FieldsOnly.class})
	@Column(unique = true)
	private String md5Sum;

	@JsonView({View.FieldsOnly.class})
	private String localFilePath;

	@JsonView({View.FieldsOnly.class})
	private Long fileSize;

	@JsonView({View.FieldsOnly.class})
	private String s3Path;
	
	@JsonView({View.FieldsOnly.class})
	private Integer recordCount;

	@JsonView({View.FieldsOnly.class})
	@Column(columnDefinition="TEXT")
	private String errorMessage;

	@ManyToOne
	private BulkLoad bulkLoad;
	
	@JsonView({View.FieldsOnly.class})
	@OneToMany(mappedBy = "bulkLoadFile", fetch = FetchType.EAGER)
	@OrderBy("loadFinished DESC")
	private List<BulkLoadFileHistory> history;

	@Transient
	@JsonView({View.FieldsOnly.class})
	public String getS3Url() {
		// TODO craft proper URL based on system
		// Get system and craft s3URL based on md5Sum
		return "https://agr-curation-files.s3.amazonaws.com/" + s3Path;
	}

	@Transient
	@JsonIgnore
	@JsonView({View.FieldsOnly.class})
	public String generateS3MD5Path() {
		if(md5Sum != null && md5Sum.length() > 0) {
			return md5Sum.charAt(0) + "/" + md5Sum.charAt(1) + "/" + md5Sum.charAt(2) + "/" + md5Sum.charAt(3) + "/" + md5Sum + "." + bulkLoad.getFileExtension() + ".gz";
		} else {
			return null;
		}
	}
}
