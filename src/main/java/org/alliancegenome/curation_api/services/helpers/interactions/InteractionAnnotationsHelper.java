package org.alliancegenome.curation_api.services.helpers.interactions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alliancegenome.curation_api.model.entities.ontology.WBPhenotypeTerm;
import org.alliancegenome.curation_api.services.ontology.WbPhenotypeTermService;
import org.apache.commons.collections.CollectionUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class InteractionAnnotationsHelper {
	
	private static final Pattern WB_PHENO_TERM = Pattern.compile("wormbase:\"(WBPhenotype:\\d+)\"");
	private static final Pattern NON_WB_ANNOT = Pattern.compile("(.*?)\\((.*?)\\)");
	
	@Inject WbPhenotypeTermService wbPhenotypeTermService;
	
	public List<String> extractPhenotypeStatements(List<String> annotations) {
		if (CollectionUtils.isEmpty(annotations))
			return null;
		
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
	
	private String extractPhenotypeStatement(String annotation) {
		if (annotation.startsWith("wormbase:")) {
			Matcher wbMatcher = WB_PHENO_TERM.matcher(annotation);
			if (!wbMatcher.find())
				return null;
			String wbPhenotypeTermCurie = wbMatcher.group(1);
			WBPhenotypeTerm wbPhenotypeTerm = wbPhenotypeTermService.findByCurieOrSecondaryId(wbPhenotypeTermCurie);
			if (wbPhenotypeTerm == null)
				return null;
			return wbPhenotypeTerm.getName();
		}
		
		List<String> statementParts = new ArrayList<>();
		String[] annotationParts = annotation.split(";");
		for (String annotationPart : annotationParts) {
			Matcher nonWbMatcher = NON_WB_ANNOT.matcher(annotationPart);
			if (nonWbMatcher.find()) {
				if (nonWbMatcher.group(1).equals("type")) {
					switch (nonWbMatcher.group(2)) {
						case "wild type":
							statementParts.add("complete rescue");
							break;
						case "partial rescue":
							statementParts.add("partial rescue");
							break;
						case "undetermined":
							statementParts.add("undetermined extend of rescue");
							break;
						default:
						Log.error("Unrecognised annotation type " + annotationPart);
					}
				}
				else {
					statementParts.add(nonWbMatcher.group(2));
				}
			}
		}
	
		if (CollectionUtils.isEmpty(statementParts))
			return null;
		
		return String.join(", ", statementParts);
	}
}
