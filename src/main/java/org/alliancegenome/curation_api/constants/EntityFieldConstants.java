package org.alliancegenome.curation_api.constants;

public final class EntityFieldConstants {

	private EntityFieldConstants() {
		// Hidden from view, as it is a utility class
	}
	
	public static final String RELATION = "relation";
	
	public static final String PRIMARY_EXTERNAL_ID = "primaryExternalId";
	public static final String MOD_INTERNAL_ID = "modInternalId";

	public static final String ASSEMBLY = "genomeAssembly.primaryExternalId";
	public static final String TAXON = "taxon.curie";
	public static final String ORGANIZATION_ABBREVIATION = "abbreviation";
	public static final String DATA_PROVIDER = "dataProvider." + ORGANIZATION_ABBREVIATION;
	public static final String SECONDARY_DATA_PROVIDER = "secondaryDataProvider." + ORGANIZATION_ABBREVIATION;

	public static final String DA_SUBJECT = "diseaseAnnotationSubject";
	public static final String EA_SUBJECT = "expressionAnnotationSubject";
	public static final String PA_SUBJECT = "phenotypeAnnotationSubject";
	public static final String EXP_EXPERIMENT_SUBJECT = "entityAssayed";
	public static final String ALLELE_ASSOCIATION_SUBJECT = "alleleAssociationSubject";
	public static final String AGM_ASSOCIATION_SUBJECT = "agmAssociationSubject";
	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT = "codingSequenceAssociationSubject";
	public static final String CONSTRUCT_ASSOCIATION_SUBJECT = "constructAssociationSubject";
	public static final String EXON_ASSOCIATION_SUBJECT = "exonAssociationSubject";
	public static final String GENE_ASSOCIATION_SUBJECT = "geneAssociationSubject";
	public static final String VARIANT_ASSOCIATION_SUBJECT = "variantAssociationSubject";
	public static final String SQTR_ASSOCIATION_SUBJECT = "sequenceTargetingReagentAssociationSubject";
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT = "transcriptAssociationSubject";
	public static final String ALLELE_CONSTRUCT_ASSOCIATION_OBJECT = "alleleConstructAssociationObject";
	public static final String DA_MODIFIER_AGMS = "diseaseGeneticModifierAgms";
	public static final String DA_MODIFIER_ALLELES = "diseaseGeneticModifierAlleles";
	public static final String DA_MODIFIER_GENES = "diseaseGeneticModifierGenes";
	public static final String ASSERTED_ALLELES = "assertedAlleles";
	public static final String ASSERTED_GENES = "assertedGenes";
	public static final String INFERRED_ALLELE = "inferredAllele";
	public static final String INFERRED_GENE = "inferredGene";
	public static final String WITH_GENE = "with";

	public static final String DA_SUBJECT_TAXON = DA_SUBJECT + "." + TAXON;
	public static final String EA_SUBJECT_TAXON = EA_SUBJECT + "." + TAXON;
	public static final String PA_SUBJECT_TAXON = PA_SUBJECT + "." + TAXON;
	public static final String EXP_EXPERIMENT_TAXON = EXP_EXPERIMENT_SUBJECT + "." + TAXON;

	public static final String ALLELE_ASSOCIATION_SUBJECT_DATA_PROVIDER = ALLELE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String AGM_ASSOCIATION_SUBJECT_DATA_PROVIDER = AGM_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT_DATA_PROVIDER = CODING_SEQUENCE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String CONSTRUCT_ASSOCIATION_SUBJECT_DATA_PROVIDER = CONSTRUCT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String EXON_ASSOCIATION_SUBJECT_DATA_PROVIDER = EXON_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String GENE_ASSOCIATION_SUBJECT_DATA_PROVIDER = GENE_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String VARIANT_ASSOCIATION_SUBJECT_DATA_PROVIDER = VARIANT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String SQTR_ASSOCIATION_SUBJECT_DATA_PROVIDER = SQTR_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT_DATA_PROVIDER = TRANSCRIPT_ASSOCIATION_SUBJECT + "." + DATA_PROVIDER;

	public static final String CODING_SEQUENCE_ASSOCIATION_SUBJECT_TAXON = CODING_SEQUENCE_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String EXON_ASSOCIATION_SUBJECT_TAXON = EXON_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String GENE_ASSOCIATION_SUBJECT_TAXON = GENE_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String TRANSCRIPT_ASSOCIATION_SUBJECT_TAXON = TRANSCRIPT_ASSOCIATION_SUBJECT + "." + TAXON;
	public static final String VARIANT_ASSOCIATION_SUBJECT_TAXON = VARIANT_ASSOCIATION_SUBJECT + "." + TAXON;

	public static final String GENOMIC_LOCATION_ASSOCIATION_OBJECT = "GenomicLocationAssociationObject";
	public static final String CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "codingSequence" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "exon" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String GENE_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "gene" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "transcript" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String VARIANT_GENOMIC_LOCATION_ASSOCIATION_OBJECT = "variant" + GENOMIC_LOCATION_ASSOCIATION_OBJECT;
	public static final String CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "codingSequence" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "exon" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "transcript" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;
	public static final String GENE_GENOMIC_LOCATION_ASSOCIATION_OBJECT_ASSEMBLY = "gene" + GENOMIC_LOCATION_ASSOCIATION_OBJECT + "." + ASSEMBLY;

	public static final String SUBJECT_GENE_DATA_PROVIDER = "subjectGene." + DATA_PROVIDER;
	public static final String SUBJECT_GENE_TAXON = "subjectGene." + TAXON;
	public static final String CLOSURE_SUBJECT = "closureSubject";
	public static final String CLOSURE_OBJECT = "closureSubject";

	public static final String SUBJECT_GENE = "subjectGene";
	public static final String SUBJECT_GENE_DATA_PROVIDER = SUBJECT_GENE + "." + DATA_PROVIDER;
	public static final String SUBJECT_GENE_TAXON = SUBJECT_GENE + "." + TAXON;
	public static final String OBJECT_GENE = "objectGene";

	public static final String AGM_AGM_ASSOCIATION_OBJECT = "agmAgmAssociationObject";
	public static final String AGM_ALLELE_ASSOCIATION_OBJECT = "agmAlleleAssociationObject";
	public static final String GENE_GENE_ASSOCIATION_OBJECT = "geneGeneAssociationObject";
	
	public static final String MAPS_TO_CHROMOSOME = "mapsToChromosome";

	public static final String VARIANT_TRANSCRIPT = "variantTranscript";

}
