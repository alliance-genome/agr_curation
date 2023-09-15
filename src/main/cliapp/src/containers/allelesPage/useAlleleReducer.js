import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		alleleSynonyms: [],
		alleleFullName: null, 
		references: [],
		inCollection: {
			name: "",
		},
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	synonymsEditingRows: {},
	fullNameEditingRows: {},
	errorMessages: {},
	synonymsErrorMessages: [],
	fullNameErrorMessages: [],
	submitted: false,
	showSynonyms: false,
	showFullName: false,
};

const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			const allele = action.value;
			//todo: refactor these two if statements (maybe a constant array of strings that list all table types, and for each run this code?)
			//this may need to include if it is an object or array
			if(allele?.alleleSynonyms){
				let clonableEntities = global.structuredClone(allele.alleleSynonyms);
				clonableEntities.forEach((entity, index) => {
					entity.dataKey = index;
					draft.synonymsEditingRows[`${entity.dataKey}`] = true;
				});

				allele.alleleSynonyms = clonableEntities;
				draft.showSynonyms = true;
			} else {
				allele.alleleSynonyms = [];
			}
			if(allele?.alleleFullName){
				allele.alleleFullName.dataKey = 0;
				draft.fullNameEditingRows[0] = true;
				draft.showFullName = true;
			} else {
				allele.alleleFullName = null;
			}
			draft.allele = allele;
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
		case 'EDIT_OBJECT':
			draft.allele[action.objectType][action.field] = action.value;
			break;
		case 'ADD_ROW':
			draft.allele[action.tableType].push(action.row);
			draft[action.editingRowsType][`${action.row.dataKey}`] = true;
			draft[action.showType]= true;
			break;
		case 'ADD_OBJECT':
			draft.allele[action.objectType] = action.value
			draft[action.editingRowsType][`${action.value.dataKey}`] = true;
			draft[action.showType]= true;
			break;
		case 'DELETE_ROW':
			draft.allele[action.tableType].splice(action.index, 1);
			if(draft.allele[action.tableType].length === 0){
				draft[action.showType] = false;
			}
			break;
		case 'DELETE_OBJECT':
			draft.allele[action.objectType] = null;
			draft[action.showType] = false;
			break;
		case 'UPDATE_ERROR_MESSAGES':
			draft[action.errorType]= action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};
			draft.synonymsErrorMessages = [];
			draft.fullNameErrorMessages = [];
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
