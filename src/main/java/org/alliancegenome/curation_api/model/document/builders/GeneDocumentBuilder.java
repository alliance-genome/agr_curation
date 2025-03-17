package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.GeneSearchResultDocument;
import org.alliancegenome.curation_api.model.document.es.GeneSummaryDocument;
import org.alliancegenome.curation_api.model.entities.AGMPhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.AllelePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.CrossReference;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.GeneDiseaseAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneExpressionAnnotation;
import org.alliancegenome.curation_api.model.entities.GeneOntologyAnnotation;
import org.alliancegenome.curation_api.model.entities.GenePhenotypeAnnotation;
import org.alliancegenome.curation_api.model.entities.associations.alleleAssociations.AlleleGeneAssociation;
import org.alliancegenome.curation_api.model.entities.ontology.AnatomicalTerm;
import org.alliancegenome.curation_api.model.entities.ontology.DOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.GOTerm;
import org.alliancegenome.curation_api.model.entities.ontology.OntologyTerm;
import org.alliancegenome.curation_api.model.entities.ontology.SOTerm;
import org.alliancegenome.curation_api.model.entities.orthology.GeneToGeneOrthologyGenerated;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.geneSlotAnnotations.GeneSynonymSlotAnnotation;
import org.apache.commons.collections.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneDocumentBuilder {

	private final static Set<String> biotypeLevel0 = Set.of(
		"protein_coding_gene",
		"pseudogene",
		"ncRNA_gene",
		"other_gene"
	);

	private final static Set<String> biotypeLevel1 = Set.of(
		"unclassified_ncRNA_gene",
		"lncRNA_gene",
		"piRNA_gene",
		"miRNA_gene",
		"snoRNA_gene",
		"tRNA_gene",
		"snRNA_gene",
		"rRNA_gene",
		"enzymatic_RNA_gene",
		"SRP_RNA_gene",
		"scRNA_gene",
		"RNase_P_RNA_gene",
		"telomerase_RNA_gene",
		"RNase_MRP_RNA_gene",
		"unclassified_gene",
		"heritable_phenotypic_marker",
		"gene_segment",
		"pseudogenic_gene_segment",
		"transposable_element_gene",
		"blocked_reading_frame");

	private final static Set<String> biotypeLevel2 = Set.of(
		"unclassified_lncRNA_gene",
		"lncRNA_gene",
		"antisense_lncRNA_gene",
		"sense_intronic_ncRNA_gene",
		"bidirectional_promoter_lncRNA",
		"sense_overlap_ncRNA_gene");

	public static GeneSearchResultDocument buildSearchResultDocument(Gene gene) {

		GeneSearchResultDocument doc = new GeneSearchResultDocument();

		doc.setCurie(gene.getPrimaryExternalId());

		if (gene.getGeneFullName() != null) {
			doc.setName(gene.getGeneFullName().getFormatText());
			if (gene.getTaxon() != null && gene.getTaxon().getSpecies() != null && gene.getTaxon().getSpecies().size() > 0) {
				doc.setNameKey(gene.getGeneFullName().getFormatText() + " (" + gene.getTaxon().getSpecies().get(0).getAbbreviation() + ")");
			} else {
				doc.setNameKey(gene.getGeneFullName().getFormatText());
			}
		}

		if (gene.getGeneType() != null) {
			SOTerm term = gene.getGeneType();
			doc.setSoTermName(term.getName());
			doc.setSoTermId(term.getCurie());
			doc.setBiotypes(term.getIsaAncestors().stream().map(OntologyTerm::getName).collect(Collectors.toSet()));
			doc.getBiotypes().add(term.getName());
			doc.setSoTermNameWithParents(new HashSet<>(doc.getBiotypes()));
			handleBioTypes(doc);
		}

		if (gene.getTaxon() != null) {
			doc.setSpecies(gene.getTaxon().getName());
		}
		if (gene.getGeneSymbol() != null) {
			doc.setSymbol(gene.getGeneSymbol().getDisplayText());
		}

		if (gene.getGeneSynonyms() != null) {
			doc.setSynonyms(gene.getGeneSynonyms().stream().map(GeneSynonymSlotAnnotation::getDisplayText).collect(Collectors.toSet()));
		}

		if (gene.getGeneSecondaryIds() != null) {
			doc.setSecondaryIds(gene.getGeneSecondaryIds().stream().map(GeneSecondaryIdSlotAnnotation::getSecondaryId).collect(Collectors.toSet()));
		}

		if (gene.getCrossReferences() != null) {
			doc.setCrossReferences(gene.getCrossReferences().stream().map(CrossReference::getReferencedCurie).collect(Collectors.toSet()));
		}

		if (gene.getGeneGenomicLocationAssociations() != null) {
			doc.setChromosomes(gene.getGeneGenomicLocationAssociations().stream().map(t -> {
				if (t.getGeneGenomicLocationAssociationObject() != null) {
					return t.getGeneGenomicLocationAssociationObject().getName();
				}
				return null;
			}).filter(Objects::nonNull).collect(Collectors.toSet()));
		}

		if (gene.getAlleleGeneAssociations() != null) {
			doc.setAlleles(new HashSet<>());
			for (AlleleGeneAssociation association : gene.getAlleleGeneAssociations()) {
				if (association.getAlleleAssociationSubject() != null && association.getRelation().getName().equals("is_allele_of")) {
					doc.getAlleles().add(association.getAlleleAssociationSubject().getAlleleSymbol().getFormatText());

					// if(association.getAlleleAssociationSubject().getAgm) {
					// TODO: Once the code for AgmAllele Associations comes thorugh will be able to
					// pick up this field
					// }
				}
			}
		}

		doc.setPhenotypeStatements(new HashSet<>());
		if (gene.getGenePhenotypeAnnotations() != null) {
			for (GenePhenotypeAnnotation annotation : gene.getGenePhenotypeAnnotations()) {
				doc.getPhenotypeStatements().add(annotation.getPhenotypeAnnotationObject());
			}
		}
		if (gene.getAllelePhenotypeInferredGeneAnnotations() != null) {
			for (AllelePhenotypeAnnotation annotation : gene.getAllelePhenotypeInferredGeneAnnotations()) {
				doc.getPhenotypeStatements().add(annotation.getPhenotypeAnnotationObject());
			}
		}
		if (gene.getAllelePhenotypeAssertedGeneAnnotations() != null) {
			for (AllelePhenotypeAnnotation annotation : gene.getAllelePhenotypeAssertedGeneAnnotations()) {
				doc.getPhenotypeStatements().add(annotation.getPhenotypeAnnotationObject());
			}
		}
		if (gene.getAgmPhenotypeInferredGeneAnnotations() != null) {
			for (AGMPhenotypeAnnotation annotation : gene.getAgmPhenotypeInferredGeneAnnotations()) {
				doc.getPhenotypeStatements().add(annotation.getPhenotypeAnnotationObject());
			}
		}
		if (gene.getAgmPhenotypeAssertedGeneAnnotations() != null) {
			for (AGMPhenotypeAnnotation annotation : gene.getAgmPhenotypeAssertedGeneAnnotations()) {
				doc.getPhenotypeStatements().add(annotation.getPhenotypeAnnotationObject());
			}
		}

		if (gene.getGeneDiseaseAnnotations() != null) {
			doc.setDiseases(new HashSet<>());
			doc.setDiseasesWithParents(new HashSet<>());
			for (GeneDiseaseAnnotation annotation : gene.getGeneDiseaseAnnotations()) {
				if (annotation.getDiseaseAnnotationObject() != null) {
					DOTerm term = annotation.getDiseaseAnnotationObject();
					for (OntologyTerm ontologyTerm : term.getIsaAncestors()) {
						doc.getDiseasesWithParents().add(ontologyTerm.getName());
					}
					doc.getDiseases().add(term.getName());
				}
			}
		}

		if (gene.getGeneToGeneOrthologyGenerateds() != null) {
			doc.setStrictOrthologySymbols(new HashSet<>());
			for (GeneToGeneOrthologyGenerated ortho : gene.getGeneToGeneOrthologyGenerateds()) {
				if (ortho.getObjectGene().getGeneDiseaseAnnotations() != null) {
					for (GeneDiseaseAnnotation annotation : ortho.getObjectGene().getGeneDiseaseAnnotations()) {
						DOTerm term = annotation.getDiseaseAnnotationObject();
						for (OntologyTerm ontologyTerm : term.getIsaAncestors()) {
							doc.getDiseasesWithParents().add(ontologyTerm.getName());
						}
						doc.getDiseases().add(term.getName());
					}
				}
				if (ortho.getStrictFilter()) {
					doc.getStrictOrthologySymbols().add(ortho.getObjectGene().getGeneSymbol().getDisplayText());
				}
			}
		}

		if (gene.getGeneOntologyAnnotations() != null) {
			doc.setCellularComponentAgrSlim(new HashSet<String>());
			doc.setCellularComponentWithParents(new HashSet<String>());
			doc.setBiologicalProcessAgrSlim(new HashSet<String>());
			doc.setBiologicalProcessWithParents(new HashSet<String>());
			doc.setMolecularFunctionAgrSlim(new HashSet<String>());
			doc.setMolecularFunctionWithParents(new HashSet<String>());
			for (GeneOntologyAnnotation annotation : gene.getGeneOntologyAnnotations()) {
				GOTerm term = annotation.getGoTerm();
				if (term.getNamespace().equals("cellular_component")) {
					doc.getCellularComponentWithParents().add(term.getName());
					if (term.getSubsets() != null && term.getSubsets().contains("goslim_agr")) {
						doc.getCellularComponentAgrSlim().add(term.getName());
					}
					for (OntologyTerm ontologyTerm : term.getIsaAncestors()) {
						doc.getCellularComponentWithParents().add(ontologyTerm.getName());
						if (ontologyTerm.getSubsets() != null && ontologyTerm.getSubsets().contains("goslim_agr")) {
							doc.getCellularComponentAgrSlim().add(ontologyTerm.getName());
						}
					}
				}
				if (term.getNamespace().equals("biological_process")) {
					doc.getBiologicalProcessWithParents().add(term.getName());
					if (term.getSubsets() != null && term.getSubsets().contains("goslim_agr")) {
						doc.getBiologicalProcessAgrSlim().add(term.getName());
					}
					for (OntologyTerm ontologyTerm : term.getIsaAncestors()) {
						doc.getBiologicalProcessWithParents().add(ontologyTerm.getName());
						if (ontologyTerm.getSubsets() != null && ontologyTerm.getSubsets().contains("goslim_agr")) {
							doc.getBiologicalProcessAgrSlim().add(ontologyTerm.getName());
						}
					}
				}
				if (term.getNamespace().equals("molecular_function")) {
					doc.getMolecularFunctionWithParents().add(term.getName());
					if (term.getSubsets() != null && term.getSubsets().contains("goslim_agr")) {
						doc.getMolecularFunctionAgrSlim().add(term.getName());
					}
					for (OntologyTerm ontologyTerm : term.getIsaAncestors()) {
						doc.getMolecularFunctionWithParents().add(ontologyTerm.getName());
						if (ontologyTerm.getSubsets() != null && ontologyTerm.getSubsets().contains("goslim_agr")) {
							doc.getMolecularFunctionAgrSlim().add(ontologyTerm.getName());
						}
					}
				}
			}
		}

		if (gene.getGeneExpressionAnnotations() != null) {
			doc.setSubcellularExpressionAgrSlim(new HashSet<String>());
			doc.setSubcellularExpressionWithParents(new HashSet<String>());
			doc.setAnatomicalExpressionWithParents(new HashSet<String>());
			doc.setWhereExpressed(new HashSet<String>());
			doc.setExpressionStages(new HashSet<String>());
			for (GeneExpressionAnnotation annotation : gene.getGeneExpressionAnnotations()) {
				if (annotation.getExpressionPattern() != null && annotation.getExpressionPattern().getWhereExpressed() != null && annotation.getExpressionPattern().getWhereExpressed().getCellularComponentTerm() != null) {
					GOTerm cellularComponentTerm = annotation.getExpressionPattern().getWhereExpressed().getCellularComponentTerm();
					if (cellularComponentTerm != null) {
						doc.getSubcellularExpressionWithParents().add(cellularComponentTerm.getName());
						if (cellularComponentTerm.getSubsets() != null && cellularComponentTerm.getSubsets().contains("goslim_agr")) {
							doc.getSubcellularExpressionAgrSlim().add(cellularComponentTerm.getName());
						}
						for (OntologyTerm ontologyTerm : cellularComponentTerm.getIsaAncestors()) {
							doc.getSubcellularExpressionWithParents().add(ontologyTerm.getName());
							if (ontologyTerm.getSubsets() != null && ontologyTerm.getSubsets().contains("goslim_agr")) {
								doc.getSubcellularExpressionAgrSlim().add(ontologyTerm.getName());
							}
						}
					}

				}

				if (annotation.getExpressionPattern() != null && annotation.getExpressionPattern().getWhereExpressed() != null && annotation.getExpressionPattern().getWhereExpressed().getAnatomicalStructure() != null) {
					AnatomicalTerm anatomicalTerm = annotation.getExpressionPattern().getWhereExpressed().getAnatomicalStructure();
					// TODO add slims to this
					if (anatomicalTerm != null) {
						doc.getAnatomicalExpressionWithParents().add(anatomicalTerm.getName());
						for (OntologyTerm ontologyTerm : anatomicalTerm.getIsaAncestors()) {
							doc.getAnatomicalExpressionWithParents().add(ontologyTerm.getName());
						}
					}
				}
				if (annotation.getWhereExpressedStatement() != null) {
					doc.getWhereExpressed().add(annotation.getWhereExpressedStatement());
				}
				if (annotation.getWhenExpressedStageName() != null) {
					doc.getExpressionStages().add(annotation.getWhenExpressedStageName());
				}
			}
		}

		return doc;

	}

	private static void handleBioTypes(GeneSearchResultDocument doc) {
		Set<String> allBiotypes = doc.getBiotypes();

		doc.setBiotype0(new HashSet<String>());
		doc.getBiotype0().add(doc.getSoTermName());
		doc.setBiotype0(new HashSet<String>(CollectionUtils.intersection(allBiotypes, biotypeLevel0)));
		doc.setBiotype1(new HashSet<String>(CollectionUtils.intersection(allBiotypes, biotypeLevel1)));
		doc.setBiotype2(new HashSet<String>(CollectionUtils.intersection(allBiotypes, biotypeLevel2)));

		// if the type is ncRNA gene and not a child, also add "unclassified ncRNA gene"
		// at level 1
		if (doc.getBiotypes().contains("ncRNA_gene") && CollectionUtils.isEmpty(doc.getBiotype1())) {
			doc.getBiotypes().add("unclassified ncRNA gene");
			doc.getBiotype1().add("unclassified ncRNA gene");
		}

		// same for lncRNA genes, but one level deeper
		if (doc.getBiotypes().contains("lncRNA_gene") && CollectionUtils.isEmpty(doc.getBiotype2())) {
			doc.getBiotypes().add("unclassified lncRNA gene");
			doc.getBiotype2().add("unclassified lncRNA gene");
		}

		if (CollectionUtils.isEmpty(doc.getBiotype0())) {
			doc.getBiotypes().add("other_gene");
			doc.getBiotype0().add("other_gene");

			if (doc.getSoTermName().equals("gene")) {
				doc.getBiotype1().add("unclassified gene");
			}
			if (doc.getSoTermName().equals("biological_region")) {
				doc.getBiotype1().add("unclassified biological region");
			}

			doc.getBiotypes().addAll(doc.getBiotype1());
		}
	}

	public GeneSummaryDocument buildSummaryDocument(Gene gene) {
		return new GeneSummaryDocument();
	}

}
