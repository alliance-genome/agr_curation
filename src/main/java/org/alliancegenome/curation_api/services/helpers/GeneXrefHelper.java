package org.alliancegenome.curation_api.services.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.CrossReferenceConstants;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.ResourceDescriptorPage;
import org.alliancegenome.curation_api.services.CrossReferenceService;
import org.alliancegenome.curation_api.services.ResourceDescriptorPageService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class GeneXrefHelper {

	@Inject ResourceDescriptorPageService rdpService;
	@Inject CrossReferenceService xrefService;
	@Inject GeneDAO geneDAO;
	
	ResourceDescriptorPage ncbiGeneOtherExpressionPage;
	ResourceDescriptorPage ncbiGeneBiogridPage;
	Map<String, ResourceDescriptorPage> expressionAtlasPageMap = new HashMap<>();
	ResourceDescriptorPage hgncGeneCardsPage;
	
	@Transactional
	public Gene addGeoCrossReference(Gene gene, String entrezCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (ncbiGeneOtherExpressionPage == null) {
			ncbiGeneOtherExpressionPage = rdpService.getPageForResourceDescriptor("NCBI_Gene", CrossReferenceConstants.GENE_OTHER_EXPRESSION_PAGE_AREA);
			if (ncbiGeneOtherExpressionPage == null) {
				return null;
			}
		}
		xref.setDisplayName("GEO");
		xref.setReferencedCurie(entrezCurie);
		xref.setResourceDescriptorPage(ncbiGeneOtherExpressionPage);
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
		if (gene.getCrossReferences() != null) {
			gene.getCrossReferences().clear();
		}
		if (updatedXrefs != null) {
			if (gene.getCrossReferences() == null) {
				gene.setCrossReferences(new ArrayList<>());
			}
			gene.getCrossReferences().addAll(updatedXrefs);
		}
		
		return geneDAO.persist(gene);
	}

	@Transactional
	public Gene addBiogridCrossReference(Gene gene, String entrezCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (ncbiGeneBiogridPage == null) {
			ncbiGeneBiogridPage = rdpService.getPageForResourceDescriptor("NCBI_Gene", CrossReferenceConstants.BIOGRID_ORCS_PAGE_AREA);
			if (ncbiGeneBiogridPage == null) {
				return null;
			}
		}
		xref.setDisplayName("BioGRID CRISPR Screen Cell Line Phenotypes");
		xref.setReferencedCurie(entrezCurie);
		xref.setResourceDescriptorPage(ncbiGeneBiogridPage);
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
		if (gene.getCrossReferences() != null) {
			gene.getCrossReferences().clear();
		}
		if (updatedXrefs != null) {
			if (gene.getCrossReferences() == null) {
				gene.setCrossReferences(new ArrayList<>());
			}
			gene.getCrossReferences().addAll(updatedXrefs);
		}
		
		return geneDAO.persist(gene);
	}
	
	@Transactional
	public Gene addExpressionAtlasXref(Gene gene, String resourceDescriptorPrefix, String referencedCurie) {
	
		CrossReference xref = new CrossReference();
		
		if (!expressionAtlasPageMap.containsKey(resourceDescriptorPrefix)) {
			ResourceDescriptorPage rdp = rdpService.getPageForResourceDescriptor(resourceDescriptorPrefix, CrossReferenceConstants.EXPRESSION_ATLAS_PAGE_AREA);
			if (rdp == null) {
				return null;
			}
			expressionAtlasPageMap.put(resourceDescriptorPrefix, rdp);
		}
		xref.setDisplayName("Expression Atlas");
		xref.setReferencedCurie(referencedCurie);
		xref.setResourceDescriptorPage(expressionAtlasPageMap.get(resourceDescriptorPrefix));
		
		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);
		
		if (gene.getCrossReferences() != null) {
			gene.getCrossReferences().clear();
		}
		if (updatedXrefs != null) {
			if (gene.getCrossReferences() == null) {
				gene.setCrossReferences(new ArrayList<>());
			}
			gene.getCrossReferences().addAll(updatedXrefs);
		}
		
		return geneDAO.persist(gene);
	}

	/**
	 * SCRUM-6455: GeneCards asked to be linked from gene pages. The link is derived rather than
	 * submitted — the HGNC id it needs is already on every human gene, so nothing has to be loaded
	 * for it, unlike the GEO and BioGRID xrefs above which take an identifier from a load file.
	 *
	 * Keyed on the HGNC id, not the HGNC symbol. GeneCards support either, but symbols are mutable:
	 * a symbol-keyed link rots silently, and after a symbol transfer it resolves to the wrong gene.
	 *
	 * The page hangs off the HGNC descriptor rather than a GeneCards one, so the curie and the
	 * descriptor agree. That is the same arrangement as NCBI_Gene's biogrid/orcs page, whose URL
	 * points at biogrid.org, and HGNC's own gene/MODinteractions pages, which point at RGD.
	 *
	 * The page is read from the database, like every resource descriptor page. Returns null when it
	 * is not there, which is the case until v0.53.0.6 has been applied to the environment. Callers
	 * must treat that as "skip", not as an error: a missing linkout must never fail a gene load.
	 */
	@Transactional
	public Gene addGeneCardsCrossReference(Gene gene, String hgncCurie) {

		CrossReference xref = new CrossReference();

		if (hgncGeneCardsPage == null) {
			hgncGeneCardsPage = rdpService.getPageForResourceDescriptor("HGNC", CrossReferenceConstants.GENECARDS_PAGE_AREA);
			if (hgncGeneCardsPage == null) {
				return null;
			}
		}
		xref.setDisplayName("GeneCards");
		xref.setReferencedCurie(hgncCurie);
		xref.setResourceDescriptorPage(hgncGeneCardsPage);
		// Set explicitly rather than left to default to NULL, as the three helpers above do. At ~44k
		// human genes it matters: crossreference_referencedcurie_trgm_idx and
		// crossreference_displayname_trgm_idx are both partial on "internal = false AND obsolete =
		// false", which NULL does not satisfy, so NULL rows would sit outside the fuzzy-search indexes.
		xref.setInternal(false);
		xref.setObsolete(false);

		List<CrossReference> updatedXrefs = xrefService.getUpdatedXrefList(List.of(xref), gene.getCrossReferences(), true);

		if (gene.getCrossReferences() != null) {
			gene.getCrossReferences().clear();
		}
		if (updatedXrefs != null) {
			if (gene.getCrossReferences() == null) {
				gene.setCrossReferences(new ArrayList<>());
			}
			gene.getCrossReferences().addAll(updatedXrefs);
		}

		return geneDAO.persist(gene);
	}
}
