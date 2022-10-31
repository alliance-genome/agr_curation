import { useImmerReducer } from "use-immer";

const initialNewVocabularyTermSetState = {
	newVocabularyTermSet: {
		name: "",
		vocabularyTermSetDescription: "",
		vocabularyTermSetVocabulary: null,
		memberTerms: [],
	},
	errorMessages: {},
	submitted: false,
	newRelationDialog: false,
};

const newVocabularyTermSetReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			draft.newVocabularyTermSet = initialNewVocabularyTermSetState.newVocabularyTermSet;
			draft.errorMessages = {};
			draft.submitted = false;
			draft.newVocabularyTermSetDialog = false;
			break;
		case 'EDIT':
			draft.newVocabularyTermSet[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			break;
		case 'OPEN_DIALOG':
			draft.newVocabularyTermSetDialog = true;
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useNewVocabularyTermSetReducer = () => {
	const [newVocabularyTermSetState, newVocabularyTermSetDispatch] = useImmerReducer(newVocabularyTermSetReducer, initialNewVocabularyTermSetState);
	return {newVocabularyTermSetState, newVocabularyTermSetDispatch};
}
