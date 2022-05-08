package org.alliancegenome.curation_api.model.entities.curationreports;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;

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
@ToString(exclude = {"curationReport"}, callSuper = true)
public abstract class CurationReportHistory extends GeneratedAuditedObject {

    @ManyToOne
    private CurationReport curationReport;

    @JsonView({View.FieldsOnly.class})
    private LocalDateTime curationReportTimestamp;

    @JsonView({View.FieldsOnly.class})
    private String pdfFilePath;
    
    @JsonView({View.FieldsOnly.class})
    private String xlsxFilePath;
    

    @JsonView({View.FieldsOnly.class})
    private String curationReportStatus;

}
