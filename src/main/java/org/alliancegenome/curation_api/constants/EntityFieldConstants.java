package org.alliancegenome.curation_api.constants;

public final class EntityFieldConstants {

	private EntityFieldConstants() {
		// Hidden from view, as it is a utility class
	}

	public static final String TAXON = "taxon.curie";
	public static final String DATA_PROVIDER = "dataProvider.sourceOrganization.abbreviation";
	public static final String SECONDARY_DATA_PROVIDER = "secondaryDataProvider.sourceOrganization.abbreviation";
	public static final String DA_SUBJECT = "diseaseAnnotationSubject";
	public static final String DA_SUBJECT_TAXON = DA_SUBJECT + "." + TAXON;
	public static final String PA_SUBJECT = "phenotypeAnnotationSubject";
	public static final String PA_SUBJECT_TAXON = PA_SUBJECT + "." + TAXON;
	public static final String ALLELE_ASSOCIATION_SUBJECT_DATA_PROVIDER = "alleleAssociationSubject." + DATA_PROVIDER;
	public static final String SQTR_ASSOCIATION_SUBJECT_DATA_PROVIDER = "sequenceTargetingReagentAssociationSubject." + DATA_PROVIDER;
	public static final String CONSTRUCT_ASSOCIATION_SUBJECT_DATA_PROVIDER = "constructAssociationSubject." + DATA_PROVIDER;
	public static final String SUBJECT_GENE_DATA_PROVIDER = "subjectGene." + DATA_PROVIDER;
	public static final String SUBJECT_GENE_TAXON = "subjectGene." + TAXON;
	public static final String EA_SUBJECT_TAXON = "expressionAnnotationSubject.taxon.curie";
}
