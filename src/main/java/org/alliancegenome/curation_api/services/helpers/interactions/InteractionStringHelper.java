package org.alliancegenome.curation_api.services.helpers.interactions;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class InteractionStringHelper {
	
	private static final Pattern PSI_MI_FORMAT = Pattern.compile("^[^:]+:\"([^\"]*)\"");
	private static final Pattern WB_VAR_ANNOTATION = Pattern.compile("wormbase:(WBVar\\d+)\\D*");
	private static final Pattern PSI_MI_FORMAT_TAXON = Pattern.compile("^taxid:(\\d+)");

	public static String getGeneMolecularInteractionUniqueId(PsiMiTabDTO dto, Gene interactorA, Gene interactorB, String interactionId, List<Reference> references) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(getGeneInteractionUniqueId(dto, interactorA, interactorB, interactionId, references, VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM));
		if (dto.getInteractionDetectionMethods() != null)
			uniqueId.addList(dto.getInteractionDetectionMethods().stream().map(dm -> extractCurieFromPsiMiFormat(dm)).collect(Collectors.toList()));
		return uniqueId.getUniqueId();
	}
	
	public static String getGeneGeneticInteractionUniqueId(PsiMiTabDTO dto, Gene interactorA, Gene interactorB, String interactionId, List<Reference> references, List<String> phenotypesOrTraits) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(getGeneInteractionUniqueId(dto, interactorA, interactorB, interactionId, references, VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM));
		if (dto.getSourceDatabaseIds() != null)
			uniqueId.addList(dto.getSourceDatabaseIds().stream().map(sd -> extractCurieFromPsiMiFormat(sd)).collect(Collectors.toList()));
		uniqueId.add(extractWBVarCurieFromAnnotations(dto.getInteractorAAnnotationString()));
		uniqueId.add(extractWBVarCurieFromAnnotations(dto.getInteractorBAnnotationString()));
		uniqueId.addList(phenotypesOrTraits);
		return uniqueId.getUniqueId();
	}
	
	public static String getGeneInteractionUniqueId(PsiMiTabDTO dto, Gene interactorA, Gene interactorB, String interactionId, List<Reference> references, String relation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(interactionId);
		if (interactorA != null)
			uniqueId.add(interactorA.getIdentifier());
		uniqueId.add(relation);
		if (interactorB != null)
			uniqueId.add(interactorB.getIdentifier());
		if (references != null)
			uniqueId.addList(references.stream().map(Reference::getCurie).collect(Collectors.toList()));
		if (dto.getInteractionTypes() != null)
			uniqueId.addList(dto.getInteractionTypes().stream().map(it -> extractCurieFromPsiMiFormat(it)).collect(Collectors.toList()));
		uniqueId.add(extractCurieFromPsiMiFormat(dto.getExperimentalRoleA()));
		uniqueId.add(extractCurieFromPsiMiFormat(dto.getExperimentalRoleB()));
		uniqueId.add(extractCurieFromPsiMiFormat(dto.getInteractorAType()));
		uniqueId.add(extractCurieFromPsiMiFormat(dto.getInteractorBType()));
		return uniqueId.getUniqueId();
	}

	public static String extractCurieFromPsiMiFormat(String psiMiString) {
		// For extracting curies from PSI-MI fields of format <prefix>:"<curie>"(<description>)
		if (StringUtils.isBlank(psiMiString))
			return null;
		
		Matcher matcher = PSI_MI_FORMAT.matcher(psiMiString);
		
		if (!matcher.find())
			return null;
		
		return matcher.group(1);
	}
	
	public static String getAllianceTaxonCurie(String psiMiTaxonString) {
		// For retrieving Alliance taxon curie from PSI-MI taxon fields
		if (StringUtils.isBlank(psiMiTaxonString))
			return null;
		
		Matcher matcher = PSI_MI_FORMAT_TAXON.matcher(psiMiTaxonString);
		
		if (!matcher.find())
			return null;
		
		return "NCBITaxon:" + matcher.group(1);
	}
	
	public static String extractWBVarCurieFromAnnotations(String annotationsString) {
		if (StringUtils.isBlank(annotationsString))
			return null;
		
		Matcher matcher = WB_VAR_ANNOTATION.matcher(annotationsString);
		
		if (!matcher.find())
			return null;
		
		return "WB:" + matcher.group(1);
	}
	
	public static String getAggregationDatabaseMITermCurie(PsiMiTabDTO dto) {
		if (CollectionUtils.isEmpty(dto.getSourceDatabaseIds()))
			return null;
		String sourceDatabaseCurie = extractCurieFromPsiMiFormat(dto.getSourceDatabaseIds().get(0));
		if (sourceDatabaseCurie == null)
			return null;
		if (sourceDatabaseCurie.equals("MI:0478") || sourceDatabaseCurie.equals("MI:0487") || sourceDatabaseCurie.equals("MI:0463")) {
			return sourceDatabaseCurie;
		}
		return "MI:0670";
	}
}
