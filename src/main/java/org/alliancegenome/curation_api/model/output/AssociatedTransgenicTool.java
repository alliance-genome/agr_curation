package org.alliancegenome.curation_api.model.output;

import java.util.List;

import org.alliancegenome.curation_api.model.entities.TransgenicTool;
import org.alliancegenome.curation_api.model.entities.associations.TransgenicToolTransgenicToolAssociation;
import org.alliancegenome.curation_api.view.CurationView;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * A tool a transgenic tool is submitted as compatible with, as the Transgenic Tools table shows it:
 * relation, tool symbol and tool identifier.
 *
 * The tool views carry these instead of the tool - tool associations, whose tools are in the
 * association view only: a tool nested in a tool view would carry its associations again and loop
 * (A -> B -> A).
 */
public record AssociatedTransgenicTool(
	@JsonView({ CurationView.TransgenicToolView.class }) String relation,
	@JsonView({ CurationView.TransgenicToolView.class }) String symbol,
	@JsonView({ CurationView.TransgenicToolView.class }) String identifier) {

	/** The associated tools of a tool's associations, in their order (a tool has a dozen at most). */
	public static List<AssociatedTransgenicTool> fromAssociations(List<TransgenicToolTransgenicToolAssociation> associations) {
		if (associations == null) {
			return List.of();
		}
		return associations.stream().map(AssociatedTransgenicTool::fromAssociation).toList();
	}

	private static AssociatedTransgenicTool fromAssociation(TransgenicToolTransgenicToolAssociation association) {
		TransgenicTool tool = association.getTransgenicToolTransgenicToolAssociationObject();
		return new AssociatedTransgenicTool(
			association.getRelation() == null ? null : association.getRelation().getName(),
			tool == null || tool.getTransgenicToolSymbol() == null ? null : tool.getTransgenicToolSymbol().getDisplayText(),
			tool == null ? null : modIdentifier(tool));
	}

	// The MOD identifier, which curators recognise, rather than the AGRKB curie getIdentifier() prefers.
	private static String modIdentifier(TransgenicTool tool) {
		if (StringUtils.isNotBlank(tool.getPrimaryExternalId())) {
			return tool.getPrimaryExternalId();
		}
		if (StringUtils.isNotBlank(tool.getModInternalId())) {
			return tool.getModInternalId();
		}
		return tool.getCurie();
	}
}
