import { relationName } from '../cassettesPage/utils';

// "name (curie)" of each term of a use slot annotation, or the curie for a term without a name.
export const useTermTexts = (transgenicToolUse) =>
	(transgenicToolUse?.uses || []).map((term) => (term?.name ? `${term.name} (${term.curie})` : term?.curie));

// "relation: symbol" of an associated tool as the search results carry it (associatedTransgenicTools),
// its identifier when it has no symbol.
export const associatedToolText = (associatedTool) =>
	`${relationName({ relation: { name: associatedTool?.relation } })}: ${associatedTool?.symbol || associatedTool?.identifier || ''}`;

// The find parameters for the associations of one tool, for the associations pop-up.
export const toolAssociationsFindParams = (transgenicToolId) => ({
	'transgenicToolAssociationSubject.id': transgenicToolId,
});
