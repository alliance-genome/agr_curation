package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.model.entities.ontology.*;

public enum OntologyBulkLoadType {
	ECO(ECOTerm.class),
	ZFA(ZFATerm.class),
	DO(DOTerm.class),
	MA(MATerm.class),
	CHEBI(CHEBITerm.class),
	XCO(XCOTerm.class),
	MP(MPTerm.class),
	DAO(DAOTerm.class),
	ZECO(ZECOTerm.class),
	WBBT(WBBTTerm.class),
	EMAPA(EMAPATerm.class),
	GO(GOTerm.class),
	SO(SOTerm.class),
	RO(ROTerm.class),
	WBLS(WBLSTerm.class),
	FBDV(FBDVTerm.class),
	MMUSDV(MMUSDVTerm.class),
	ZFS(ZFSTerm.class),
	XBA_XBS(null),
	XBA(XBATerm.class),
	XBS(XBSTerm.class),
	XPO(XPOTerm.class),
	ATP(ATPTerm.class),
	XBED(XBEDTerm.class),
	XSMO(XSMOTerm.class)
	;
	
	private Class<? extends OntologyTerm> clazz;
	
	private OntologyBulkLoadType(Class<? extends OntologyTerm> clazz) {
		this.clazz = clazz;
	}
	
	public Class<? extends OntologyTerm> getClazz() {
		return clazz;
	}

}
