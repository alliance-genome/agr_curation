package org.alliancegenome.curation_api.enums;

public enum PsiMiTabPrefixEnum {
	wormbase("WB", true),
	flybase("FB", true),
	uniprotkb("UniProtKB", false),
	intact("INTACT", false),
	entrez_gene_locuslink("NCBI_Gene", false);

	public String alliancePrefix;
	public Boolean isModPrefix;
	
	private PsiMiTabPrefixEnum(String alliancePrefix, Boolean isModPrefix) {
		this.alliancePrefix = alliancePrefix;
		this.isModPrefix = isModPrefix;
	}

	public static PsiMiTabPrefixEnum findByPsiMiTabPrefix(String psiMiTabPrefix) {
		psiMiTabPrefix = psiMiTabPrefix.replace(" ", "_");
		psiMiTabPrefix = psiMiTabPrefix.replace("/", "_");
		
		for (PsiMiTabPrefixEnum prefix : values()) {
			if (prefix.name().equals(psiMiTabPrefix))
				return prefix;
		}
		
		return null;
	}
}
