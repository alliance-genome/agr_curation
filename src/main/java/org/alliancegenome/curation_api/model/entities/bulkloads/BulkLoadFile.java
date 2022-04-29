package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.GeneratedAuditedObject;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoad"})
public class BulkLoadFile extends GeneratedAuditedObject {
    
    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private BulkLoadStatus status;

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
