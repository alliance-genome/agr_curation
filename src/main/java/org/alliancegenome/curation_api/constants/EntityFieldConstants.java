package org.alliancegenome.curation_api.constants;

public final class EntityFieldConstants {

	public static final String DATA_PROVIDER = "dataProvider.sourceOrganization.abbreviation";
	public static final String SECONDARY_DATA_PROVIDER = "secondaryDataProvider.sourceOrganization.abbreviation";
	public static final String SUBJECT_BIOLOGICAL_ENTITY_DATA_PROVIDER = "subjectBiologicalEntity." + DATA_PROVIDER;
	public static final String SUBJECT_REAGENT_DATA_PROVIDER = "subjectReagent." + DATA_PROVIDER;
	public static final String SUBJECT_BIOLOGICAL_ENTITY_TAXON = "subjectBiologicalEntity.taxon.curie";
	public static final String TAXON = "taxon.curie";

}