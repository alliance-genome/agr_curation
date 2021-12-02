package org.alliancegenome.curation_api.services.helpers;

import lombok.Data;

@Data
public class GenericOntologyLoadConfig {

    // These values should not be changed here
    // Only change them in the bulk controller
    // that is going to make use of the GenericOntologyLoader
    private boolean loadWithoutDefaultNameSpace = false;
    private String altNameSpace = null;
    
}
