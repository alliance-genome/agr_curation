// The relation name without a trailing ontology id, e.g. "expresses (RO:0002292)" -> "expresses".
export const relationName = (association) => {
	const name = association?.relation?.name || '';
	return name.indexOf(' (RO:') !== -1 ? name.substring(0, name.indexOf(' (RO:')) : name;
};

export const identifierOf = (entity) => entity?.primaryExternalId || entity?.modInternalId || entity?.curie || '';

export const genomicEntityLabel = (genomicEntity) =>
	genomicEntity?.geneSymbol?.displayText ||
	genomicEntity?.alleleSymbol?.displayText ||
	genomicEntity?.name ||
	identifierOf(genomicEntity);

export const transgenicToolLabel = (transgenicTool) =>
	transgenicTool?.transgenicToolSymbol?.displayText || identifierOf(transgenicTool);

// "symbol (identifier)" for the Transgenic Tool Components pop-up, or just the identifier without a symbol.
export const transgenicToolDisplayValue = (transgenicTool) => {
	const symbol = transgenicTool?.transgenicToolSymbol?.displayText;
	return symbol ? `${symbol} (${identifierOf(transgenicTool)})` : identifierOf(transgenicTool);
};

// "relation: label" for each association, the label taken from the association's object.
export const associationStrings = (associations, objectField, label) =>
	(associations || []).map((association) => `${relationName(association)}: ${label(association?.[objectField])}`);

export const useStrings = (cassetteUses) =>
	(cassetteUses || []).flatMap((cassetteUse) =>
		(cassetteUse?.uses || []).map((use) => (use?.name ? `${use.name} (${use.curie})` : use?.curie))
	);
