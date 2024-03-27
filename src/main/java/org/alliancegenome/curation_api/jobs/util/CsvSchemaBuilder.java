package org.alliancegenome.curation_api.jobs.util;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvSchemaBuilder {

	public static CsvSchema psiMiTabSchema() {
		CsvSchema schema = CsvSchema.builder()
				.setColumnSeparator('\t')
				.setArrayElementSeparator("|")
				.setAllowComments(true)
				.setNullValue("-")
				.disableQuoteChar()
				.addColumn("interactorAIdentifier")
				.addColumn("interactorBIdentifier")
				.addColumn("interactorAAlternativeId")
				.addColumn("interactorBAlternativeId")
				.addColumn("interactorAAliases")
				.addColumn("interactorBAliases")
				.addColumn("interactionDetectionMethods")
				.addColumn("authors")
				.addColumn("publicationIds")
				.addColumn("interactorATaxonId")
				.addColumn("interactorBTaxonId")
				.addColumn("interactionTypes")
				.addColumn("sourceDatabaseIds")
				.addColumn("interactionIds")
				.addColumn("confidenceScore")
				.addColumn("complexExpansion")
				.addColumn("biologicalRoleA")
				.addColumn("biologicalRoleB")
				.addColumn("experimentalRoleA")
				.addColumn("experimentalRoleB")
				.addColumn("interactorAType")
				.addColumn("interactorBType")
				.addColumn("interactorAXrefs")
				.addColumn("interactorBXrefs")
				.addColumn("interactionXrefs")
				.addColumn("interactorAAnnotationString")
				.addColumn("interactorBAnnotationString")
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
