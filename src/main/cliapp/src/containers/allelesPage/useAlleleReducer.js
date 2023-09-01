import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		alleleSynonyms: [],
		references: [],
		inCollection: {
			name: "",
		},
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	synonymsEditingRows: {},
	errorMessages: {},
	synonymsErrorMessages: [],
	submitted: false,
	showSynonyms: false,
};

const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			draft.allele = action.value;
			break;
		case 'RESET':
			draft.allele = initialAlleleState.allele;
			draft.errorMessages = {};
			draft.submitted = false;
			break;
		case 'EDIT':
			draft.allele[action.field] = action.value;
			break;
		case 'EDIT_ROW':
			draft.allele[action.tableType][action.index][action.field] = action.value;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft.errorMessages = action.errorMessages;
			break;
		case 'ADD_ROW':
			// draft.newAnnotation.relatedNotes.push(
			// 	{
			// 		dataKey: action.count,
			// 		noteType: {
			// 			name : ""
			// 		},
			// 		freeText: "",
			// 	}
			// )
			// draft.relatedNotesEditingRows[`${action.count}`] = true;
			draft[action.showType]= true;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft[action.errorType]= action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};
			draft.synonymsErrorMessages = [];
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
