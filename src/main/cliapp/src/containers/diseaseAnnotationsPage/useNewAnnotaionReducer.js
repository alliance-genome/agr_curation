import { useImmerReducer } from "use-immer";

const initialNewAnnotationState = {
	newAnnotation: {},
	errorMessages: {},
	submitted: false,
	newAnnotationDialog: false,
};

const newAnnotationReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			draft.newAnnotation = initialNewAnnotationState.newAnnotation;
			draft.errorMessages = {};
			draft.submitted = false;
			draft.newAnnotationDialog = false;
			break;
		case 'EDIT':
			draft.newAnnotation[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			break;
		case 'OPEN_DIALOG':
			draft.newAnnotationDialog = true;
			break;
		case 'CLEAR':
			draft.newAnnotation = initialNewAnnotationState.newAnnotation;
			draft.errorMessages = {};
			draft.submitted = false;
			draft.newAnnotationDialog = true;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useNewAnnotationReducer = () => {
	const [newAnnotationState, newAnnotationDispatch] = useImmerReducer(newAnnotationReducer, initialNewAnnotationState);
	return {newAnnotationState, newAnnotationDispatch};
}
