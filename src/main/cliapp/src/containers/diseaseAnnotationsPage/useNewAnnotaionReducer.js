import { useImmerReducer } from "use-immer";

const initialNewAnnotationState = {
	newAnnotation: {
		subject: {
			curie: "",
		},
		diseaseRelation: {
			name: "",
		},
		negated: false,
		object: {
			curie: "",
		},
		singleReference: {
			curie: "",
		},
		evidenceCodes: [],
		with: [],
		relatedNotes: [],
	},
	errorMessages: {},
	relatedNotesErrorMessages: [],
	submitted: false,
	newAnnotationDialog: false,
	showRelatedNotes: false,
	isValid: false,
};

const newAnnotationReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			return initialNewAnnotationState;
		case 'EDIT':
			draft.newAnnotation[action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			draft.isValid = false;
			break;
		case 'UPDATE_RELATED_NOTES_ERROR_MESSAGES':
			draft.relatedNotesErrorMessages = action.errorMessages;
			draft.isValid = false;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};
			draft.relatedNotesErrorMessages = [];
			draft.isValid = false;
			break;
		case 'OPEN_DIALOG':
			draft.newAnnotationDialog = true;
			break;
		case 'CLEAR':
			return {...initialNewAnnotationState, newAnnotationDialog: true}
		case 'ADD_NEW_NOTE':
			draft.newAnnotation.relatedNotes.push(
				{
					dataKey: action.count,
					noteType: {
						name : ""
					},
					freeText: "",
				}
			)
			draft.showRelatedNotes = true;
			break;
		case 'DELETE_NOTE':
			draft.newAnnotation.relatedNotes.splice(action.index, 1);
			if(draft.newAnnotation.relatedNotes.length === 0){
				draft.showRelatedNotes = false;
			}
			break;
		case 'EDIT_NOTE':
			draft.newAnnotation.relatedNotes[action.index][action.field] = action.value;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useNewAnnotationReducer = () => {
	const [newAnnotationState, newAnnotationDispatch] = useImmerReducer(newAnnotationReducer, initialNewAnnotationState);
	return {newAnnotationState, newAnnotationDispatch};
}
