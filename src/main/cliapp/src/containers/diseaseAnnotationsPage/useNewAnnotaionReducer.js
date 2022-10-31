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
		conditionRelations: [],
		geneticSex: {},
		diseaseQualifiers: [],
		sgdStrainBackground: "",
		annotationType: {},
		diseaseGeneticModifierRelation: {},
		diseaseGeneticModifier: "",
		internal: false
	},
	errorMessages: {},
	relatedNotesErrorMessages: [],
	exConErrorMessages: [],
	submitted: false,
	newAnnotationDialog: false,
	showRelatedNotes: false,
	showConditionRelations: false,
	isValid: false,
};

const newAnnotationReducer = (draft, action) => {
	switch (action.type) {
		case 'RESET':
			return initialNewAnnotationState;
		case 'EDIT':
			draft.newAnnotation[action.field] = action.value;
			break;
		case 'EDIT_EXPERIMENT':
			if (typeof action.value === "object") {
				draft.newAnnotation.conditionRelations[0] = action.value;
			} else {
				if(draft.newAnnotation.conditionRelations && draft.newAnnotation.conditionRelations[0])
					draft.newAnnotation.conditionRelations[0].handle = action.value;
			}
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft[action.errorType]= action.errorMessages;
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
		case 'ADD_NEW_RELATION':
			draft.newAnnotation.conditionRelations.push(
				{
					dataKey: action.count,
					conditions: [],
				}
			)
			draft.showConditionRelations = true;
			break;
		case 'DELETE_ROW':
			draft.newAnnotation[action.tableType].splice(action.index, 1);
			if(draft.newAnnotation[action.tableType].length === 0){
				draft[action.showType] = false;
			}
			break;
		case 'EDIT_ROW':
			draft.newAnnotation[action.tableType][action.index][action.field] = action.value;
			break;
		default:
			throw Error('Unknown action: ' + action.type);
	}
};

export const useNewAnnotationReducer = () => {
	const [newAnnotationState, newAnnotationDispatch] = useImmerReducer(newAnnotationReducer, initialNewAnnotationState);
	return {newAnnotationState, newAnnotationDispatch};
}
