package org.alliancegenome.curation_api.model.entities.bulkloads;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseGeneratedEntity;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkLoad.BulkLoadStatus;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;

import com.fasterxml.jackson.annotation.*;

import lombok.*;

@Audited
@Entity
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"bulkLoad"})
public class BulkLoadFile extends BaseGeneratedEntity {

    @Enumerated(EnumType.STRING)
    private BulkLoadStatus status;
    
    @Column(unique = true)
    private String md5Sum;
    
    private String localFilePath;
    
    private Long fileSize;

    @Column(columnDefinition="TEXT")
    private String errorMessage;
    
    @ManyToOne
    private BulkLoad bulkLoad;
    
    @Transient
    @JsonProperty("s3Url")
    public String getS3Url() {
        // TODO craft proper URL based on system
        // Get system and craft s3URL based on md5Sum
        return "" + generateS3Path();
    }
    
    @Transient
    @JsonProperty("s3Path")
    public String generateS3Path() {
        return md5Sum.charAt(0) + "/" + md5Sum.charAt(1) + "/" + md5Sum.charAt(2) + "/" + md5Sum.charAt(3) + "/" + md5Sum + ".gz";
    }
}
