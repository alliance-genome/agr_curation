import { useImmerReducer } from 'use-immer';

const initialNewConditionState = {
	newCondition: {
		internal: null,
		obsolete: 'false',
		conditionClass: null,
		conditionId: null,
		conditionTaxon: null,
		conditionGeneOntology: null,
		conditionChemical: null,
		conditionAnatomy: null,
		conditionQuantity: '',
		conditionFreeText: '',
	},
	errorMessages: {},
	submitted: false,
	newConditionDialog: false,
};

const newConditionReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			draft.newCondition = initialNewConditionState.newCondition;
			draft.errorMessages = {};
			draft.submitted = false;
			draft.newConditionDialog = false;
			break;
		case 'EDIT':
			draft.newCondition[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			break;
		case 'OPEN_DIALOG':
			draft.newConditionDialog = true;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useNewConditionReducer = () => {
	const [newConditionState, newConditionDispatch] = useImmerReducer(newConditionReducer, initialNewConditionState);
	return { newConditionState, newConditionDispatch };
};
