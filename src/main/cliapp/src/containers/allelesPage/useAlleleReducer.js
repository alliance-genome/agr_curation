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
		case 'ADD_ROW':
			draft.allele[action.tableType].push(action.row);
			draft[action.editingRowsType][`${action.row.dataKey}`] = true;
			draft[action.showType]= true;
			break;
		case 'DELETE_ROW':
			draft.allele[action.tableType].splice(action.index, 1);
			if(draft.allele[action.tableType].length === 0){
				draft[action.showType] = false;
			}
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
