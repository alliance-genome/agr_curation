import { useDetailFormReducer } from '../../../hooks/useDetailFormReducer';

// The cassette's sections whose rows are edited in a table: 'object' holds one row, 'table' a list. Its component
// lists are not among them: each is a sub-resource of its own (useCassetteComponentList).
const ENTITY_TYPES = {
	cassetteSymbol: 'object',
	cassetteFullName: 'object',
	cassetteSynonyms: 'table',
	cassetteUses: 'table',
	references: 'table',
};

/**
 * Cassette detail page form state and its dispatch (SCRUM-6747). The cassette's own fields and lists are held here
 * and saved with the cassette.
 *
 * @returns {{cassetteState: Object, cassetteDispatch: Function}}
 */
export const useCassetteReducer = () => {
	const [cassetteState, cassetteDispatch] = useDetailFormReducer('cassette', ENTITY_TYPES);
	return { cassetteState, cassetteDispatch };
};
