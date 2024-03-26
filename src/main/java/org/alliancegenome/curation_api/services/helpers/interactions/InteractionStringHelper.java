package org.alliancegenome.curation_api.services.helpers.interactions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.VocabularyConstants;
import org.alliancegenome.curation_api.enums.PsiMiTabPrefixEnum;
import org.alliancegenome.curation_api.model.entities.Reference;
import org.alliancegenome.curation_api.model.ingest.dto.fms.PsiMiTabDTO;
import org.alliancegenome.curation_api.services.helpers.UniqueIdGeneratorHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public abstract class InteractionStringHelper {
	
	private static final Pattern PSI_MI_FORMAT = Pattern.compile("^[^:]+:\"([^\"]*)\"");
	private static final Pattern WB_VAR_ANNOTATION = Pattern.compile("wormbase:(WBVar\\d+)\\D*");

	public static String getGeneMolecularInteractionUniqueId(PsiMiTabDTO dto, String interactionId, List<Reference> references) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(getGeneInteractionUniqueId(dto, interactionId, references, VocabularyConstants.GENE_MOLECULAR_INTERACTION_RELATION_TERM));
		if (dto.getInteractionDetectionMethods() != null)
			uniqueId.addAll(dto.getInteractionDetectionMethods().stream().map(dm -> extractCurieFromPsiMiFormat(dm)).collect(Collectors.toList()));
		return uniqueId.getUniqueId();
	}
	
	public static String getGeneGeneticInteractionUniqueId(PsiMiTabDTO dto, String interactionId, List<Reference> references) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(getGeneInteractionUniqueId(dto, interactionId, references, VocabularyConstants.GENE_GENETIC_INTERACTION_RELATION_TERM));
		if (dto.getSourceDatabaseIds() != null)
			uniqueId.addAll(dto.getSourceDatabaseIds().stream().map(sd -> extractCurieFromPsiMiFormat(sd)).collect(Collectors.toList()));
		uniqueId.add(extractWBVarCurieFromAnnotations(dto.getInteractorAAnnotations()));
		uniqueId.addAll(extractPhenotypeStatements(dto.getInteractionAnnotations()));
		return uniqueId.getUniqueId();
	}
	
	public static String getGeneInteractionUniqueId(PsiMiTabDTO dto, String interactionId, List<Reference> references, String relation) {
		UniqueIdGeneratorHelper uniqueId = new UniqueIdGeneratorHelper();
		uniqueId.add(interactionId);
		uniqueId.add(PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractorAIdentifier()));
		uniqueId.add(relation);
		uniqueId.add(PsiMiTabPrefixEnum.getAllianceIdentifier(dto.getInteractorBIdentifier()));
		if (references != null)
			uniqueId.addAll(references.stream().map(Reference::getCurie).collect(Collectors.toList()));
		if (dto.getInteractionTypes() != null)
			uniqueId.addAll(dto.getInteractionTypes().stream().map(it -> extractCurieFromPsiMiFormat(it)).collect(Collectors.toList()));
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
	
	public static String extractWBVarCurieFromAnnotations(String annotationsString) {
		if (annotationsString.isBlank())
			return null;
		
		Matcher matcher = WB_VAR_ANNOTATION.matcher(annotationsString);
		
		if (!matcher.find())
			return null;
		
		return "WB:" + matcher.group(1);
	}
	
	public static String getAggregationDatabaseMITermCurie(PsiMiTabDTO dto) {
		String sourceDatabaseCurie = extractCurieFromPsiMiFormat(dto.getSourceDatabaseIds().get(0));
		if (sourceDatabaseCurie == null)
			return null;
		if (sourceDatabaseCurie.equals("MI:0478") || sourceDatabaseCurie.equals("MI:0487") || sourceDatabaseCurie.equals("MI:0463")) {
			return sourceDatabaseCurie;
		}
		return "MI:0670";
	}
	
	public static List<String> extractPhenotypeStatements(List<String> annotations) {
		List<String> statements = new ArrayList<>();
		for (String annotation : annotations) {
			String statement = extractPhenotypeStatement(annotation);
			if (statement != null)
				statements.add(statement);
		}
		
		if (CollectionUtils.isEmpty(statements))
			return null;
		
		return statements;
	}
	
	private static String extractPhenotypeStatement(String annotation) {
		// TODO: implement method to extract phenotype statement from annotations
		// See code in agr_loader genetic_interaction_etl.py line 365
		return null;
	}
	
	public static String getAllianceCurie(String psiMiTabIdentifier) {
		// For converting curies with prefixes that differ from those used at AGR, e.g. wormbase:WBGene000001
		String[] psiMiTabIdParts = psiMiTabIdentifier.split(":");
		if (psiMiTabIdParts.length != 2)
			return null;
		
		PsiMiTabPrefixEnum prefix = PsiMiTabPrefixEnum.findByPsiMiTabPrefix(psiMiTabIdParts[0]);
		if (prefix == null)
			return null;
		
		return prefix.alliancePrefix + ":" + psiMiTabIdParts[1];
	}
	
	// TODO: Can remove this method once loading interactions where interactors are referenced by xref
	public static Boolean isAllianceInteractor(String psiMiTabIdentifier) {
		if (StringUtils.isBlank(psiMiTabIdentifier))
			return false;
		
		String[] psiMiTabIdParts = psiMiTabIdentifier.split(":");
		if (psiMiTabIdParts.length != 2)
			return false;
		
		PsiMiTabPrefixEnum prefix = PsiMiTabPrefixEnum.findByPsiMiTabPrefix(psiMiTabIdParts[0]);
		if (prefix == null)
			return false;
		
		return prefix.isModPrefix;
	}
}
