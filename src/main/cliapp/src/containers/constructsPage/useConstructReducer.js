import { useImmerReducer } from 'use-immer';
import { generateCrossRefSearchFields, generateComponentSearchFields } from './utils';

const initialConstructState = {
	construct: {},
	errorMessages: {},
	submitted: false,
};

const constructReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			const construct = action.value;
			generateCrossRefSearchFields(construct.references);
			generateComponentSearchFields(construct.constructGenomicEntityAssociations);
			draft.construct = construct;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useConstructReducer = () => {
	const [constructState, constructDispatch] = useImmerReducer(constructReducer, initialConstructState);
	return { constructState, constructDispatch };
};
