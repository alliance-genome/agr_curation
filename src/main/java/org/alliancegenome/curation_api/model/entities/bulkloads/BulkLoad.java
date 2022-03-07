package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.BaseGeneratedEntity;
import org.alliancegenome.curation_api.enums.OntologyBulkLoadType;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import lombok.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
@JsonSubTypes({ 
    @Type(value = BulkFMSLoad.class, name = "BulkFMSLoad"), 
    @Type(value = BulkURLLoad.class, name = "BulkURLLoad"), 
    @Type(value = BulkManualLoad.class, name = "BulkManualLoad") 
})

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(exclude = {"group"}, callSuper = true)
public abstract class BulkLoad extends BaseGeneratedEntity {

    @JsonView({View.FieldsOnly.class})
    private String name;

    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private BulkLoadStatus status;

    @JsonView({View.FieldsOnly.class})
    @Column(columnDefinition="TEXT")
    private String errorMessage;

    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private BackendBulkLoadType backendBulkLoadType;

    @JsonView({View.FieldsOnly.class})
    @Enumerated(EnumType.STRING)
    private OntologyBulkLoadType ontologyType;

    @ManyToOne
    private BulkLoadGroup group;

    @JsonView({View.FieldsOnly.class})
    private String fileExtension;

    @JsonView({View.FieldsOnly.class})
    @OneToMany(mappedBy = "bulkLoad", fetch = FetchType.EAGER)
    private List<BulkLoadFile> loadFiles;

    public enum BulkLoadStatus {

        SCHEDULED_PENDING,
        SCHEDULED_STARTED,
        SCHEDULED_RUNNING,

        FORCED_PENDING,
        FORCED_STARTED,
        FORCED_RUNNING,



        FAILED,
        STOPPED,
        FINISHED,

        ;

        public boolean isRunning() {
            return this == SCHEDULED_RUNNING || this == FORCED_RUNNING;
        }

        public boolean isPending() {
            return this == FORCED_PENDING || this == SCHEDULED_PENDING;
        }

        public boolean isStarted() {
            return this == FORCED_STARTED || this == SCHEDULED_STARTED;
        }
        
        public boolean isNotRunning() {
            return this == FAILED || this == STOPPED || this == FINISHED;
        }
        
        public BulkLoadStatus getNextStatus() {
            if(this == BulkLoadStatus.FORCED_PENDING) return BulkLoadStatus.FORCED_STARTED;
            if(this == BulkLoadStatus.FORCED_STARTED) return BulkLoadStatus.FORCED_RUNNING;

            if(this == BulkLoadStatus.SCHEDULED_PENDING) return BulkLoadStatus.SCHEDULED_STARTED;
            if(this == BulkLoadStatus.SCHEDULED_STARTED) return BulkLoadStatus.SCHEDULED_RUNNING;

            return FAILED;
        }

        public boolean isForced() {
            return this == FORCED_PENDING || this == FORCED_STARTED || this == FORCED_RUNNING;
        }
    }

    public enum BackendBulkLoadType {
        GENE_DTO, ALLELE_DTO, AGM_DTO, DISEASE_ANNOTATION_DTO, DISEASE_ANNOTATION,
        GENE, ALLELE, AGM, AGM_DISEASE_ANNOTATION, ALLELE_DISEASE_ANNOTATION, GENE_DISEASE_ANNOTATION,
        ONTOLOGY, MOLECULE, FULL_INGEST
        ;
    }




}
