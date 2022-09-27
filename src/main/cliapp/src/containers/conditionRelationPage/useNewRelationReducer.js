import { useImmerReducer } from "use-immer";

const initialNewRelationState = {
	newRelation: {
		handle: "",
		singleReference: {
			curie: "",
		},
		conditionRelationType: {name: ""},
		conditions: [],
	},
	errorMessages: {},
	submitted: false,
	newRelationDialog: false,
};

const newRelationReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			draft.newRelation = initialNewRelationState.newRelation;
			draft.errorMessages = {};
			draft.submitted = false;
			draft.newRelationDialog = false;
			break;
		case 'EDIT':
			draft.newRelation[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			break;
		case 'OPEN_DIALOG':
			draft.newRelationDialog = true;
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useNewRelationReducer = () => {
	const [newRelationState, newRelationDispatch] = useImmerReducer(newRelationReducer, initialNewRelationState);
	return {newRelationState, newRelationDispatch};
}
