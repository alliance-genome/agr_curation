package org.alliancegenome.curation_api.enums;

import org.apache.commons.lang3.StringUtils;

public enum PsiMiTabPrefixEnum {
	wormbase("WB", true),
	flybase("FB", true),
	uniprotkb("UniProtKB", false),
	intact("INTACT", false),
	entrez_gene_locuslink("NCBI_Gene", false),
	pubmed("PMID", false),
	doi("DOI", false),
	mi("MI", false),
	ensembl("ENSEMBL", false),
	refseq("RefSeq", false);

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
	
	public static String getAllianceIdentifier(String psiMiTabIdentifier) {
		if (StringUtils.isBlank(psiMiTabIdentifier))
			return null;
		
		String[] psiMiTabIdParts = psiMiTabIdentifier.split(":");
		if (psiMiTabIdParts.length != 2)
			return psiMiTabIdentifier;
		
		PsiMiTabPrefixEnum prefix = findByPsiMiTabPrefix(psiMiTabIdParts[0]);
		if (prefix == null)
			return psiMiTabIdentifier;
		
		return prefix.alliancePrefix + ":" + psiMiTabIdParts[1];
	}
}
