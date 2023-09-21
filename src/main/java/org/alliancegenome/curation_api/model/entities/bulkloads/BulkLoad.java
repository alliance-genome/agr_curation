package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.BackendBulkLoadType;
import org.alliancegenome.curation_api.enums.JobStatus;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = BulkFMSLoad.class, name = "BulkFMSLoad"), @Type(value = BulkURLLoad.class, name = "BulkURLLoad"), @Type(value = BulkManualLoad.class, name = "BulkManualLoad") })

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "group" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public abstract class BulkLoad extends GeneratedAuditedObject {

	@JsonView({ View.FieldsOnly.class })
	private String name;

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private JobStatus bulkloadStatus = JobStatus.STOPPED;

	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private BackendBulkLoadType backendBulkLoadType;

	@JsonView({ View.FieldsOnly.class })
	@Enumerated(EnumType.STRING)
	private OntologyBulkLoadType ontologyType;

	@ManyToOne
	private BulkLoadGroup group;

	@JsonView({ View.FieldsOnly.class })
	private String fileExtension;

	@JsonView({ View.FieldsOnly.class })
	@OneToMany(mappedBy = "bulkLoad", fetch = FetchType.EAGER)
	@OrderBy("dateUpdated DESC")
	private List<BulkLoadFile> loadFiles;

}
