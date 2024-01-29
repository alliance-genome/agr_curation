import { useImmerReducer } from "use-immer";
import { addDataKey, generateCrossRefSearchFields, generateCurieSearchFields } from "./utils";
import { getUniqueItemsByProperty } from "../../utils/utils";

const initialAlleleState = {
	allele: {
		taxon: {
			curie: "",
		},
		alleleSynonyms: [],
		alleleFullName: null,
		alleleSecondaryIds: [],
		alleleSymbol: null,
		alleleMutationTypes: [],
		alleleInheritanceModes: [],
		alleleFunctionalImpacts: [],
		alleleNomenclatureEvents: [],
		alleleGeneAssociations: [],
		alleleDatabaseStatus: null,
		alleleGermlineTransmissionStatus: null,
		references: [],
		inCollection: {
			name: "",
		},
		relatedNotes: [],
		isExtinct: false,
		internal: false,
		obsolete: false,
	},
	entityStates: {
		alleleSynonyms: {
			field: 'alleleSynonyms',
			endpoint: 'allelesynonymslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleFullName: {
			field: 'alleleFullName',
			endpoint: 'allelefullnameslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "object",
		},
		alleleSecondaryIds: {
			field: 'alleleSecondaryIds',
			endpoint: 'allelesecondaryidslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table"
		},
		alleleSymbol: {
			field: 'alleleSymbol',
			endpoint: 'allelesymbolslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "object",
		},
		alleleNomenclatureEvents: {
			field: 'alleleNomenclatureEvents',
			endpoint: 'allelenomenclatureeventslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleMutationTypes: {
			field: 'alleleMutationTypes',
			endpoint: 'allelemutationtypeslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleInheritanceModes: {
			field: 'alleleInheritanceModes',
			endpoint: 'alleleinheritancemodeslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleFunctionalImpacts: {
			field: 'alleleFunctionalImpacts',
			endpoint: 'allelefunctionalimpactslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleGermlineTransmissionStatus: {
			field: 'alleleGermlineTransmissionStatus',
			endpoint: 'allelegermlinetransmissionstatusslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "object",
		},
		alleleDatabaseStatus: {
			field: 'alleleDatabaseStatus',
			endpoint: 'alleledatabasestatusslotannotation',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "object",
		},
		relatedNotes: {
			field: 'relatedNotes',
			endpoint: 'note',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		references: {
			field: 'references',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
		alleleGeneAssociations: {
			field: 'alleleGeneAssociations',
			show: false,
			errorMessages: {},
			editingRows: {},
			type: "table",
		},
	},
	errorMessages: {},
	submitted: false,
};

const processTable = (field, allele, draft) => {
	if(!allele) return;

	if(!allele[field]) {
		allele[field] = [];
		return; 
	}

	let clonableEntities = global.structuredClone(allele[field]);
	clonableEntities.forEach((entity, index) => {
		addDataKey(entity);
		draft.entityStates[field].editingRows[`${entity.dataKey}`] = true;
	});

	allele[field] = clonableEntities;
	draft.entityStates[field].show = true;
}

const processDisplayTable = (field, allele, draft) => {
	if(!allele) return;

	if(!allele[field]) {
		allele[field] = [];
		return; 
	}

	draft.entityStates[field].show = true;
}
const processObject = (field, allele, draft) => {
	if(!allele) return;

	if(!allele[field]) return; 

	addDataKey(allele[field]);
	draft.entityStates[field].editingRows[allele[field].dataKey] = true;
	draft.entityStates[field].show = true;
}


const alleleReducer = (draft, action) => {
	switch (action.type) {
		case 'SET':
			const allele = action.value;
			generateCrossRefSearchFields(allele.references);
			generateCurieSearchFields(allele.alleleGeneAssociations, 'evidence');

			let states = Object.values(draft.entityStates);

			states.forEach((state) => {
				if(state.type === "table") processTable(state.field, allele, draft); 
				if(state.type === "object") processObject(state.field, allele, draft); 
				if(state.type === "display") processDisplayTable(state.field, allele, draft); 
			})

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
			draft.allele[action.entityType][action.index][action.field] = action.value;
			break;
		case 'EDIT_FILTERABLE_ROW': 
			const row = draft.allele[action.entityType].find((row) => row.dataKey === action.dataKey);
			if(row) {
				row[action.field] = action.value;
			};
			break;
		case 'REPLACE_ROW': 
			const index = draft.allele[action.entityType].findIndex((row) => row.dataKey === action.dataKey);
			if(index !== -1){
				draft.allele[action.entityType][index] = action.newRow;
			}
			break;
		case 'EDIT_OBJECT': 
			draft.allele[action.entityType][action.field] = action.value;
			break;
		case 'ADD_ROW': 
			draft.allele[action.entityType].unshift(action.row);
			if (action.entityType === 'references') {
				draft.allele[action.entityType] = getUniqueItemsByProperty(draft.allele[action.entityType], 'curie');
			}
			draft.entityStates[action.entityType].editingRows[`${action.row.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'ADD_OBJECT': 
			draft.allele[action.entityType] = action.value
			draft.entityStates[action.entityType].editingRows[`${action.value.dataKey}`] = true;
			draft.entityStates[action.entityType].show = true;
			break;
		case 'DELETE_ROW':
			draft.allele[action.entityType] = draft.allele[action.entityType].filter(row => row.dataKey !== action.dataKey);
			if (draft.allele[action.entityType].length === 0) {
				draft.entityStates[action.entityType].show = false;
			}
			break;

		case 'DELETE_OBJECT': 
			draft.allele[action.entityType] = null;
			draft.entityStates[action.entityType].show = false;
			break;
		case 'UPDATE_ERROR_MESSAGES': 
			draft.errorMessages = action.errorMessages;
			break;
		case 'UPDATE_TABLE_ERROR_MESSAGES': 
			draft.entityStates[action.entityType].errorMessages = action.errorMessages;
			break;
		case 'SUBMIT':
			draft.submitted = true;
			draft.errorMessages = {};

			states = Object.values(draft.entityStates);

			states.forEach((state) => {
				state.errorMessages = {};

			})
			break;
		default:
      throw Error('Unknown action: ' + action.type);
	}
};

export const useAlleleReducer = () => {
	const [alleleState, alleleDispatch] = useImmerReducer(alleleReducer, initialAlleleState);
	return {alleleState, alleleDispatch};
}
