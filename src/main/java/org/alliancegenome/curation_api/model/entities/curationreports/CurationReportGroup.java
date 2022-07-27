package org.alliancegenome.curation_api.model.entities.curationreports;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString
public class CurationReportGroup extends GeneratedAuditedObject {
	
	@JsonView({View.FieldsOnly.class})
	private String name;
	
	@JsonView({View.FieldsAndLists.class})
	@OneToMany(mappedBy = "curationReportGroup")
	private List<CurationReport> curationReports;
}
