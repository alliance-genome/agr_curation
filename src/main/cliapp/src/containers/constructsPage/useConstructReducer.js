import { useDetailFormReducer } from '../../hooks/useDetailFormReducer';

// The construct's sections whose rows are edited in a table: 'object' holds one row. Its lists are not among them:
// each is a sub-resource of its own (useConstructSubResources).
const ENTITY_TYPES = {
	constructSymbol: 'object',
	constructFullName: 'object',
};

/**
 * Construct detail page form state and its dispatch. The construct's own fields are held here and saved with the
 * construct.
 *
 * @returns {{constructState: Object, constructDispatch: Function}}
 */
export const useConstructReducer = () => {
	const [constructState, constructDispatch] = useDetailFormReducer('construct', ENTITY_TYPES);
	return { constructState, constructDispatch };
};
