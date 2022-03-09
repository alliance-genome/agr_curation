package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.model.entities.ontology.*;

public enum OntologyBulkLoadType {
    ECO(EcoTerm.class),
    ZFA(ZfaTerm.class),
    DO(DOTerm.class),
    MA(MATerm.class),
    CHEBI(CHEBITerm.class),
    XCO(XcoTerm.class),
    MP(MPTerm.class),
    DAO(DAOTerm.class),
    ZECO(ZecoTerm.class),
    WBBT(WBbtTerm.class),
    EMAPA(EMAPATerm.class),
    GO(GOTerm.class),
    SO(SOTerm.class),
    WBLS(WBlsTerm.class),
    FBDV(FBdvTerm.class),
    MMUSDV(MmusDvTerm.class),
    ZFS(ZFSTerm.class)
    ;
    
    private Class<? extends OntologyTerm> clazz;
    
    private OntologyBulkLoadType(Class<? extends OntologyTerm> clazz) {
        this.clazz = clazz;
    }
    
    public Class<? extends OntologyTerm> getClazz() {
        return clazz;
    }

}
