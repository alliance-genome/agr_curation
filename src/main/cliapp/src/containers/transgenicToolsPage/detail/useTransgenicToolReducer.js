import { useDetailFormReducer } from '../../../hooks/useDetailFormReducer';

// The tool's sections whose rows are edited in a table: 'object' holds one row, 'table' a list.
const ENTITY_TYPES = {
	transgenicToolSymbol: 'object',
	transgenicToolFullName: 'object',
	transgenicToolSynonyms: 'table',
	transgenicToolUses: 'table',
	references: 'table',
	relatedNotes: 'table',
	crossReferences: 'table',
};

/**
 * Transgenic tool detail page form state and its dispatch (SCRUM-6655). The tool's own fields and lists are
 * held here and saved with the tool; its tool associations are a sub-resource of their own
 * (useTransgenicToolAssociations).
 *
 * @returns {{transgenicToolState: Object, transgenicToolDispatch: Function}}
 */
export const useTransgenicToolReducer = () => {
	const [transgenicToolState, transgenicToolDispatch] = useDetailFormReducer('transgenicTool', ENTITY_TYPES);
	return { transgenicToolState, transgenicToolDispatch };
};
