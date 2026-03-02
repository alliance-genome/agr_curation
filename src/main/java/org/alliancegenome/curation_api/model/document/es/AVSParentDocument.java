package org.alliancegenome.curation_api.model.document.es;

import java.util.HashSet;
import java.util.List;

import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.Variant;

import lombok.Data;

@Data
public abstract class AVSParentDocument extends ESDocument {
	protected Allele allele;
	protected HashSet<String> geneIds;
	protected String alterationType;
	protected Integer alterationTypeSortOrder;
	protected Boolean hasPhenotype;
	protected Boolean hasDisease;
	private List<Variant> variants;
}
