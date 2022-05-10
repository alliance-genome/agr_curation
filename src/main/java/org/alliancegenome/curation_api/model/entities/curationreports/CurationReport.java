package org.alliancegenome.curation_api.model.entities.curationreports;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.alliancegenome.curation_api.base.entity.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"curationReportGroup", "curationReportHistory"}, callSuper = true)
public class CurationReport extends GeneratedAuditedObject {

    @JsonView({View.FieldsOnly.class})
    private String name;

    @JsonView({View.FieldsOnly.class})
    private String curationReportStatus;

    @JsonView({View.FieldsOnly.class})
    private String cronSchedule;
    
    @JsonView({View.FieldsOnly.class})
    private Boolean scheduleActive;
    
    @ManyToOne
    private CurationReportGroup curationReportGroup;

    @JsonView({View.FieldsOnly.class})
    private String birtReportFilePath;
    
    @JsonView({View.FieldsAndLists.class})
    @OneToMany(mappedBy = "curationReport", fetch = FetchType.EAGER)
    @OrderBy("curationReportTimestamp DESC")
    private List<CurationReportHistory> curationReportHistory;

}
