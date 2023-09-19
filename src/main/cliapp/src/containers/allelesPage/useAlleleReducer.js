import { useImmerReducer } from "use-immer";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		alleleSynonyms: [],
		alleleFullName: null, 
		alleleMutationTypes: [],
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
	mutationTypesEditingRows: {},
	errorMessages: {},
	synonymsErrorMessages: [],
	fullNameErrorMessages: [],
	mutationTypesErrorMessages: [],
	submitted: false,
	showSynonyms: false,
	showFullName: false,
	showMutationTypes: false,
};

const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			const allele = action.value;

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

			if(allele?.alleleMutationTypes){
				let clonableEntities = global.structuredClone(allele.alleleMutationTypes);
				clonableEntities.forEach((entity, index) => {
					entity.dataKey = index;
					draft.mutationTypesEditingRows[`${entity.dataKey}`] = true;
				});

				allele.alleleMutationTypes = clonableEntities;
				draft.showMutationTypes = true;
			} else {
				allele.alleleMutationTypes = [];
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
			draft.mutationTypesErrorMessages = [];
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
