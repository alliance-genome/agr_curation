package org.alliancegenome.curation_api.model.entities.bulkloads;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Audited
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = { "bulkLoadFileHistory", "exception" }, callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.1", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
public class BulkLoadFileException extends GeneratedAuditedObject {

	// TODO: define in LinkML once class definition matured

	@JsonView({ View.FieldsOnly.class })
	@Convert(converter = JsonBinaryType.class)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private ObjectUpdateExceptionData exception;

	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BulkLoadFileHistory bulkLoadFileHistory;

}
