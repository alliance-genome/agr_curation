import { relationName, transgenicToolLabel } from '../cassettesPage/utils';

// "name (curie)" of each term of a use slot annotation, or the curie for a term without a name.
export const useTermTexts = (transgenicToolUse) =>
	(transgenicToolUse?.uses || []).map((term) => (term?.name ? `${term.name} (${term.curie})` : term?.curie));

// "relation: symbol" of a transgenic tool - transgenic tool association, the associated tool's
// identifier when it has no symbol.
export const toolAssociationText = (association) =>
	`${relationName(association)}: ${transgenicToolLabel(association?.transgenicToolTransgenicToolAssociationObject)}`;
