package org.alliancegenome.curation_api.model.ingest.dto.fms;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PsiMiTabDTO extends BaseDTO {

	private String interactorAIdentifier;
	private String interactorBIdentifier;
	private String interactorAAlternativeId;
	private String interactorBAlternativeId;
	private String interactorAliasesA;
	private String interactorAliasesB;
	private String interactorDetectionMethods;
	private String authors;
	private String publicationId;
	private String interactorTaxonIdA;
	private String interactorTaxonIdB;
	private String interactionTypes;
	private String sourceDatabaseIds;
	private String interactionIds;
	private String confidenceScore;
	private String complexExpansion;
	private String biologicalRoleA;
	private String biologicalRoleB
	private String experimentalRoleA;
	private String experimentalRoleB;
	private String interactorAXref;
	private String interactorBXref;
	private String interactionXref;
	private String interactorAAnnotations;
	private String interactorBAnnotations;
	private String interactionAnnotations;
	private String hostOrganismTaxonId;
	private String interactionParameters;
	private String creationDate;
	private String updateDate;
	private String interactorAChecksum;
	private String interactorBChecksum;
	private String interactionChecksum;
	private Boolean negativeInteraction;
	private String interactorAFeatures;
	private String interactorBFeatures;
	private String interactorAStoichiometry;
	private String interactorBStoichiometry;
	private String interactorAParticpantIdentificationMethod;
	private String interactorBParticpantIdentificationMethod;
	
}
