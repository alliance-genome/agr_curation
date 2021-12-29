package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.model.entities.ontology.*;
import org.alliancegenome.curation_api.services.helpers.GenericOntologyLoadConfig;

public enum OntologyBulkLoadType {
    ZECO(ZecoTerm.class, (new GenericOntologyLoadConfig())),
    ECO(EcoTerm.class, new GenericOntologyLoadConfig()),
    GO(GOTerm.class, new GenericOntologyLoadConfig()),
    DO(DOTerm.class, new GenericOntologyLoadConfig()),
    //SO(SOTerm.class),
    CHEBI(CHEBITerm.class, new GenericOntologyLoadConfig()),
    MP(MPTerm.class, new GenericOntologyLoadConfig()),
    MA(MATerm.class, new GenericOntologyLoadConfig());
    
    private Class<?> clazz;
    private GenericOntologyLoadConfig config;
    
    private OntologyBulkLoadType(Class<?> clazz, GenericOntologyLoadConfig config) {
        this.clazz = clazz;
        this.config = config;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
    
    public GenericOntologyLoadConfig getConfig() {
        return config;
    }
}
