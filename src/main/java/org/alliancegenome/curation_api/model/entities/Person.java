package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Entity
@Data
public class Person extends GeneratedAuditedObject {

    @JsonView({View.FieldsOnly.class})
    private String firstName;
    @JsonView({View.FieldsOnly.class})
    private String lastName;
    
    @JsonView({View.FieldsOnly.class})
    @Column(unique = true)
    private String email;
    
    @JsonView({View.FieldsOnly.class})
    private String apiToken;
    @JsonView({View.FieldsOnly.class})
    private String modId;
    @JsonView({View.FieldsOnly.class})
    private String uniqueId;


}
