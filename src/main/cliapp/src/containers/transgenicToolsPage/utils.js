import { relationName } from '../cassettesPage/utils';

// "name (curie)" of each term of a use slot annotation, or the curie for a term without a name.
export const useTermTexts = (transgenicToolUse) =>
	(transgenicToolUse?.uses || []).map((term) => (term?.name ? `${term.name} (${term.curie})` : term?.curie));

// "relation: symbol" of an associated tool as the search results carry it (associatedTransgenicTools),
// its identifier when it has no symbol.
export const associatedToolText = (associatedTool) =>
	`${relationName({ relation: { name: associatedTool?.relation } })}: ${associatedTool?.symbol || associatedTool?.identifier || ''}`;

// A tool's associations as the search results summarise them (associatedTransgenicTools), as the API's
// AssociatedTransgenicTool builds them: the relation, and the other tool's symbol and MOD identifier.
export const toAssociatedTransgenicTools = (associations) =>
	(associations ?? []).map((association) => {
		const tool = association?.transgenicToolTransgenicToolAssociationObject;
		return {
			relation: association?.relation?.name ?? null,
			symbol: tool?.transgenicToolSymbol?.displayText ?? null,
			identifier: tool?.primaryExternalId || tool?.modInternalId || tool?.curie || null,
		};
	});

// The find parameters for the associations of one tool, for the associations pop-up.
export const toolAssociationsFindParams = (transgenicToolId) => ({
	'transgenicToolAssociationSubject.id': transgenicToolId,
});
