package org.alliancegenome.curation_api.jobs.util;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvSchemaBuilder {

	public static CsvSchema psiMiTabSchema() {
		CsvSchema schema = CsvSchema.builder()
				.setArrayElementSeparator("|")
				.addColumn("interactorIdA")
				.addColumn("interactorIdB")
				.addColumn("alternativeInteractorIdA")
				.addColumn("alternativeInteractorIdV")
				.addColumn("interactorAliasesA")
				.addColumn("interactorAliasesB")
				.addColumn("interactorDetectionMethods")
				.addColumn("authors")
				.addColumn("publicationId")
				.addColumn("interactorTaxonIdA")
				.addColumn("interactorTaxonIdB")
				.addColumn("interactionTypes")
				.addColumn("sourceDatabaseIds")
				.addColumn("interactionIds")
				.addColumn("confidenceScore")
				.addColumn("complexExpansion")
				.addColumn("biologicalRoleA")
				.addColumn("biologicalRoleB")
				.addColumn("experimentalRoleA")
				.addColumn("experimentalRoleB")
				.addColumn("interactorAXref")
				.addColumn("interactorBXref")
				.addColumn("interactionXref")
				.addColumn("interactorAAnnotations")
				.addColumn("interactorBAnnotations")
				.addColumn("interactionAnnotations")
				.addColumn("hostOrganismTaxonId")
				.addColumn("interactionParameters")
				.addColumn("creationDate")
				.addColumn("updateDate")
				.addColumn("interactorAChecksum")
				.addColumn("interactorBChecksum")
				.addColumn("interactionChecksum")
				.addColumn("negativeInteraction")
				.addColumn("interactorAFeatures")
				.addColumn("interactorBFeatures")
				.addColumn("interactorAStoichiometry")
				.addColumn("interactorBStoichiometry")
				.addColumn("interactorAParticpantIdentificationMethod")
				.addColumn("interactorBParticpantIdentificationMethod")
				.build();
		
		return schema;
	}
}
