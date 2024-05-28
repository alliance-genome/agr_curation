package org.alliancegenome.curation_api.model.entities.bulkloads;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@ToString(exclude = { "bulkLoadFileHistory", "exception" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "bulkloadfileexception_bulkLoadFileHistory_index", columnList = "bulkLoadFileHistory_id"),
		@Index(name = "bulkloadfileexception_createdby_index", columnList = "createdBy_id"),
		@Index(name = "bulkloadfileexception_updatedby_index", columnList = "updatedBy_id")
	}
)
//TODO: define this class in LinkML once class definition matured
public class BulkLoadFileException extends AuditedObject {

	@JsonView({ View.FieldsOnly.class })
	@JdbcTypeCode(SqlTypes.JSON)
	private ObjectUpdateExceptionData exception;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BulkLoadFileHistory bulkLoadFileHistory;

}
