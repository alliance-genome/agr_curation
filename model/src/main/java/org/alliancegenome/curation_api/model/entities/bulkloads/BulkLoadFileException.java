package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.alliancegenome.curation_api.constants.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.exceptions.ObjectUpdateException.ObjectUpdateExceptionData;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
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
@ToString(exclude = {"bulkLoadFileHistory", "exception"}, callSuper = true)
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
@AGRCurationSchemaVersion(min="1.2.1", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={AuditedObject.class})
public class BulkLoadFileException extends GeneratedAuditedObject {
	
	// TODO: define in LinkML once class definition matured
	
	@Type(type = JsonTypes.JSON_BIN)
	@JsonView({View.BulkLoadFileHistory.class})
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	private ObjectUpdateExceptionData exception;
	
	@ManyToOne
	@OnDelete(action = OnDeleteAction.CASCADE)
	private BulkLoadFileHistory bulkLoadFileHistory;
	
}
