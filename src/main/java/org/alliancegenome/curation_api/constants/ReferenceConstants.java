package org.alliancegenome.curation_api.constants;

import java.util.List;

public final class ReferenceConstants {
	private ReferenceConstants() {
		// Hidden from view, as it is a utility class
	}
	public static final List<String> primaryXrefOrder = List.of("PMID", "FB", "MGI", "RGD", "SGD", "WB", "XB", "ZFIN");
	
	public static final String RGD_OMIM_ORPHANET_REFERENCE = "AGRKB:101000000829523";

}