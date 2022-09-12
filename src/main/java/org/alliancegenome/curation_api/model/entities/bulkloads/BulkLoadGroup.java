package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
//@ToString(exclude = {"loads"})
@AGRCurationSchemaVersion("1.2.4")
public class BulkLoadGroup extends GeneratedAuditedObject {
	
	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@JsonView({View.FieldsOnly.class})
	@OneToMany(mappedBy = "group")
	private List<BulkLoad> loads;
}
