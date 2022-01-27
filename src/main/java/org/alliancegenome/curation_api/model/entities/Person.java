package org.alliancegenome.curation_api.model.entities;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.entity.*;

import lombok.Data;

@Entity
@Data
public class Person extends BaseGeneratedEntity {

    private String firstName;
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String apiToken;
    private String modId;
    private String uniqueId;


}
