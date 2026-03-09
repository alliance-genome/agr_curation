package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;

import org.alliancegenome.curation_api.model.entities.Allele;

import lombok.Data;

@Data
public abstract class AVSParentDocument extends ESDocument {
	protected Allele allele;
	protected HashSet<String> geneIds;
	protected String alterationType;
	protected Integer alterationTypeSortOrder;
	protected Boolean hasPhenotype;
	protected Boolean hasDisease;
}
