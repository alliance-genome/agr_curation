package org.alliancegenome.curation_api.enums;

import org.alliancegenome.curation_api.model.entities.ontology.ATPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.CHEBITerm;
import org.alliancegenome.curation_api.model.entities.ontology.DAOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.EMAPATerm;
import org.alliancegenome.curation_api.model.entities.ontology.FBDVTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MATerm;
import org.alliancegenome.curation_api.model.entities.ontology.MMUSDVTerm;
import org.alliancegenome.curation_api.model.entities.ontology.MPTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ROTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.WBBTTerm;
import org.alliancegenome.curation_api.model.entities.ontology.WBLSTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XBATerm;
import org.alliancegenome.curation_api.model.entities.ontology.XBEDTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XBSTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XCOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XPOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.XSMOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZECOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZFATerm;
import org.alliancegenome.curation_api.model.entities.ontology.ZFSTerm;

public enum OntologyBulkLoadType {
	ECO(ECOTerm.class), ZFA(ZFATerm.class), DO(DOTerm.class), MA(MATerm.class), CHEBI(CHEBITerm.class), XCO(XCOTerm.class), MP(MPTerm.class), DAO(DAOTerm.class), ZECO(ZECOTerm.class),
	WBBT(WBBTTerm.class), EMAPA(EMAPATerm.class), GO(GOTerm.class), SO(SOTerm.class), RO(ROTerm.class), WBLS(WBLSTerm.class), FBDV(FBDVTerm.class), MMUSDV(MMUSDVTerm.class), ZFS(ZFSTerm.class),
	XBA_XBS(null), XBA(XBATerm.class), XBS(XBSTerm.class), XPO(XPOTerm.class), ATP(ATPTerm.class), XBED(XBEDTerm.class), XSMO(XSMOTerm.class);

	private Class<? extends OntologyTerm> clazz;

	private OntologyBulkLoadType(Class<? extends OntologyTerm> clazz) {
		this.clazz = clazz;
	}

	public Class<? extends OntologyTerm> getClazz() {
		return clazz;
	}

}
