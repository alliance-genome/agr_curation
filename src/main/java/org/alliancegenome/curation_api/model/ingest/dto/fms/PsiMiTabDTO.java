package org.alliancegenome.curation_api.model.ingest.dto.fms;

import java.util.List;

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
	private List<String> interactorAAliases;
	private List<String> interactorBAliases;
	private List<String> interactionDetectionMethods;
	private List<String> authors;
	private List<String> publicationIds;
	private String interactorATaxonId;
	private String interactorBTaxonId;
	private List<String> interactionTypes;
	private List<String> sourceDatabaseIds;
	private List<String> interactionIds;
	private String confidenceScore;
	private String complexExpansion;
	private String biologicalRoleA;
	private String biologicalRoleB
	private String experimentalRoleA;
	private String experimentalRoleB;
	private String interactorAType;
	private String interactorBType;
	private List<String> interactorAXrefs;
	private List<String> interactorBXrefs;
	private List<String> interactionXrefs;
	private String interactorAAnnotations;
	private String interactorBAnnotations;
	private List<String> interactionAnnotations;
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
