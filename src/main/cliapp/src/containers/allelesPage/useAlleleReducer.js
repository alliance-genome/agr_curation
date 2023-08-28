import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		synonyms: [],
		references: [],
		inCollection: {
			name: "",
		},
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	errorMessages: {},
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
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
