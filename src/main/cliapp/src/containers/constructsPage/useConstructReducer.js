import { useImmerReducer } from 'use-immer';

const initialConstructState = {
	construct: {
	},
	errorMessages: {},
	submitted: false,
};

const constructReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			draft.construct = action.value;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useConstructReducer = () => {
	const [constructState, constructDispatch] = useImmerReducer(constructReducer, initialConstructState);
	return { constructState, constructDispatch };
};
