import { confirmDialog } from 'primereact/confirmdialog';
import { SORT_FIELDS } from '../constants/SortFields';

import { FIELD_SETS} from '../constants/FilterFields';

export function returnSorted(event, originalSort) {

	let found = false;
	let replace = false;
	let newSort = [...originalSort];

	if (event.multiSortMeta.length > 0) {
		newSort.forEach((o) => {
			if (o.field === event.multiSortMeta[0].field) {
				if (o.order === event.multiSortMeta[0].order) {
					replace = true;
					found = true;
				} else {
					o.order = event.multiSortMeta[0].order;
					found = true;
				}
			}
		});
	} else {
		newSort = [];
	}

	if (!found) {
		return newSort.concat(event.multiSortMeta);
	} else {
		if (replace) {
			return event.multiSortMeta;
		} else {
			return newSort;
		}
	}
}

export function trimWhitespace(value) {
	return value?.replace(/\s{2,}/g, ' ').trim();
}

export function filterColumns(columns, selectedColumnNames) {
	const filteredColumns = columns.filter((col) => {
		return selectedColumnNames?.includes(col.header);
	})
	return filteredColumns;
};

export function orderColumns(columns, orderedColumnNames) {
	if(!orderedColumnNames) return columns;
	let orderedColumns = [];
	orderedColumnNames.forEach((columnName) => {
		var column = columns.find(col => col.header === columnName); // Once column names change it can't be assumed that everyones settings are changed.
		if(column) {
			orderedColumns.push(column);
		}
	});
	return orderedColumns;
};

export function reorderArray(array, from, to) {
	const item = array.splice(from, 1);
	array.splice(to, 0, item[0]);
	return array;
};

export function setDefaultColumnOrder(columns, dataTable, defaultColumnOptions, deletionEnabled = false, tableState) {
	let initalColumnOrderObjects = [];
	let initalColumnOrderFields = [];

	defaultColumnOptions.forEach((option) => {
		initalColumnOrderObjects.push(
			columns.find((column) => {
				return column.header === option;
			})
		);
	});

	initalColumnOrderFields = initalColumnOrderObjects.map(column => column.field);

	if (deletionEnabled) initalColumnOrderFields.unshift('delete');

	initalColumnOrderFields.unshift('rowEditor');

	const newState = {
		first: tableState.first,
		rows: tableState.rows,
		multisortmeta: tableState.multisortmeta,
		filters: tableState.filters,
		columnWidths: "",
		tableWidth: "",
		columnOrder: initalColumnOrderFields,
	};

	dataTable.current.restoreTableState(newState);
}

// ToDo: Create enumeration
export function getEntityType(entity) {
	if (entity.curie && entity.curie.startsWith("AGR:AGR-Reference-")) {
		return 'Literature';
	}
	if (entity.conditionSummary) {
		return 'Experiment Condition';
	}
	return 'Unknown Entity'
}

export function getRefStrings(referenceItems) {
	if (!referenceItems)
		return;

	let refStrings = [];
	referenceItems.forEach((referenceItem) => {refStrings.push(getRefString(referenceItem))});

	return refStrings.sort();
}

export function getRefString(referenceItem) {
	if (!referenceItem)
		return;

	if (!referenceItem.cross_references && !referenceItem.crossReferences)
		return referenceItem.curie;

	let xrefCuries = [];
	if (referenceItem.cross_references) {
		referenceItem.cross_references.forEach((x,i) => xrefCuries.push(x.curie));
	} else {
		referenceItem.crossReferences.forEach((x,i) => xrefCuries.push(x.referencedCurie));
	}

	if (xrefCuries.length === 0)
		return referenceItem.curie;

	if (xrefCuries.length === 1)
		return xrefCuries[0] + ' (' + referenceItem.curie + ')';

	let sortedCuries = [];
	if (indexWithPrefix(xrefCuries, 'PMID:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'PMID:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'FB:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'FB:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'MGI:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'MGI:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'RGD:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'RGD:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'SGD:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'SGD:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'WB:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'WB:'), 1));
	}
	if (indexWithPrefix(xrefCuries, 'ZFIN:') > -1) {
		sortedCuries.push(xrefCuries.splice(indexWithPrefix(xrefCuries, 'ZFIN:'), 1));
	} 
	if (xrefCuries.length > 0) {
		sortedCuries = sortedCuries.concat(xrefCuries.sort());
	}
	sortedCuries.push(referenceItem.curie);
	
	let primaryXrefCurie = sortedCuries.splice(0, 1);

	return primaryXrefCurie + ' (' + sortedCuries.join('|') + ')';
}

function indexWithPrefix(array, prefix) {

	for (var i = 0; i < array.length; i++) {
		if (array[i].startsWith(prefix)) {
			return i;
		}
	}
	return -1;
}


export function genericConfirmDialog({ header, message, accept, reject }){
	confirmDialog({
		message,
		header,
		acceptClassName: 'p-button-danger',
		icon: 'pi pi-info-circle',
		accept,
		reject,
	});

}

function containsMatch(inputValue, selectedItem) {
	for (const part of inputValue.split(/[^a-z0-9]/i)) {
		if (selectedItem.indexOf(part) !== -1)
			 return 1;
	}
	return 0;
}

export function filterDropDownObject(inputValue, object){
	const trimmedValue = trimWhitespace(inputValue.toLowerCase());
	let _object = global.structuredClone(object);
	
	if (_object.geneSystematicName) {
		if (containsMatch(trimmedValue, _object.geneSystematicName.displayText.toString().toLowerCase()) == 0)
			_object = { ..._object, geneSystematicName: {}};
	}

	if (_object.synonyms?.length > 0) {
		const filteredSynonyms = [];
		_object.synonyms.forEach((synonym) => {
			let selectedItem = synonym.name ? synonym.name.toString().toLowerCase() : synonym.toString().toLowerCase();
			if (containsMatch(trimmedValue, selectedItem) == 1)
				filteredSynonyms.push(synonym);
		});
		_object = { ..._object, synonyms: filteredSynonyms };
	}

	if (_object.crossReferences?.length > 0) {
		const filteredCrossReferences = [];
		_object.crossReferences.forEach((xref) => {
			if (containsMatch(trimmedValue, xref.displayName.toString().toLowerCase()) == 1)
				filteredCrossReferences.push(xref);
		});
		_object = { ..._object, crossReferences: filteredCrossReferences };
	}

	if (_object.cross_references?.length > 0) {
		const filteredLitSystemCrossReferences = [];
		_object.cross_references.forEach((xref) => {
			if (containsMatch(trimmedValue, xref.curie.toString().toLowerCase()) == 1)
				filteredLitSystemCrossReferences.push(xref);
		});
		_object = { ..._object, cross_references: filteredLitSystemCrossReferences };
	}

	if (_object.secondaryIdentifiers?.length > 0) {
		const filteredSecondaryIdentifiers = [];
		_object.secondaryIdentifiers.forEach((sid) => {
			if (containsMatch(trimmedValue, sid.toString().toLowerCase()) == 1)
				filteredSecondaryIdentifiers.push(sid);
		});
		_object = { ..._object, secondaryIdentifiers: filteredSecondaryIdentifiers };
	}

	if (_object.geneSynonyms?.length > 0) {
		const filteredGeneSynonyms = [];
		_object.geneSynonyms.forEach((syn) => {
			if (containsMatch(trimmedValue, syn.displayText.toString().toLowerCase()) == 1)
				filteredGeneSynonyms.push(syn);
		});
		_object = { ..._object, geneSynonyms: filteredGeneSynonyms };
	}

	if (_object.geneSecondaryIds?.length > 0) {
		const filteredGeneSecondaryIds = [];
		_object.geneSecondaryIds.forEach((sid) => {
			if (containsMatch(trimmedValue, sid.secondaryId.toString().toLowerCase()) == 1)
				filteredGeneSecondaryIds.push(sid);
		});
		_object = { ..._object, geneSecondaryIds: filteredGeneSecondaryIds };
	}

	if (_object.alleleSynonyms?.length > 0) {
		const filteredAlleleSynonyms = [];
		_object.alleleSynonyms.forEach((syn) => {
			if (containsMatch(trimmedValue, syn.displayText.toString().toLowerCase()) == 1)
				filteredAlleleSynonyms.push(syn);
		});
		_object = { ..._object, alleleSynonyms: filteredAlleleSynonyms };
	}

	if (_object.alleleSecondaryIds?.length > 0) {
		const filteredAlleleSecondaryIds = [];
		_object.alleleSecondaryIds.forEach((sid) => {
			if (containsMatch(trimmedValue, sid.secondaryId.toString().toLowerCase()) == 1)
				filteredAlleleSecondaryIds.push(sid);
		});
		_object = { ..._object, alleleSecondaryIds: filteredAlleleSecondaryIds };
	}

	return _object;
}

export function onSelectionOver(event, item, query, op, setAutocompleteHoverItem) {
	const _item = filterDropDownObject(query, item)
	setAutocompleteHoverItem(_item);
	op.current.show(event);
}


export function autocompleteSearch(searchService, endpoint, filterName, filter, setSuggestions, otherFilters={}, applyObsoleteFilter=true) {
	const obsoleteFilter = applyObsoleteFilter && endpoint !== 'literature-reference' ? {
			obsoleteFilter: {
				"obsolete": {
					queryString: false
				}
			}
		} : {};
	searchService.search(endpoint, 15, 0, [], {[filterName]: filter, ...otherFilters, ...obsoleteFilter})
		.then((data) => {
			if (data.results?.length > 0) {
				 setSuggestions(data.results);
			} else {
				setSuggestions([]);
			}
		});
}

export function buildAutocompleteFilter(event, autocompleteFields) {
	let filter = {};
	autocompleteFields.forEach(field => {
		filter[field] = {
			queryString: event.query,
			tokenOperator: "AND",
			useKeywordFields: true
		}
	})

	return filter;
}

export function defaultAutocompleteOnChange(rowProps, event, fieldName, setFieldValue, subField="curie") {
	
	const index = rowProps.props.rows ? rowProps.rowIndex % rowProps.props.rows : rowProps.rowIndex;
	
	let updatedRows = [...rowProps.props.value];
	
	if (!event.target.value) {
		updatedRows[index][fieldName] = null;
		setFieldValue('');
		return;
	}
	
	if (typeof event.target.value === "object") {
		updatedRows[index][fieldName] = event.target.value;
		setFieldValue(updatedRows[rowProps.rowIndex][fieldName]?.[subField]);
	} else {
		updatedRows[index][fieldName] = {};
		updatedRows[index][fieldName][subField] = event.target.value;
		setFieldValue(updatedRows[index][fieldName]?.[subField]);
	}
}

export function multipleAutocompleteOnChange(rowProps, event, fieldName, setFieldValue) {
	let updatedRows = [...rowProps.props.value];
	const index = rowProps.props.rows ? rowProps.rowIndex % rowProps.props.rows : rowProps.rowIndex;
	if (!event.target.value) {
		updatedRows[index][fieldName] = null;
		setFieldValue('');
		return;
	}

	updatedRows[index][fieldName] = event.target.value;
	setFieldValue(updatedRows[index][fieldName]);
}

export function validateBioEntityFields(updatedRow, setUiErrorMessages, event, setIsEnabled, closeRowRef, areUiErrors) {
	const bioEntityFieldNames = ["subject", "sgdStrainBackground", "assertedAllele"];

	bioEntityFieldNames.forEach((field) => {
		if(updatedRow[field] && Object.keys(updatedRow[field]).length === 1){
			const errorObject = {
				severity: "error",
				message: "Must select from autosuggest"
			}
			setUiErrorMessages((uiErrorMessages) => {
				const _uiErrorMessages = global.structuredClone(uiErrorMessages);
				if (!_uiErrorMessages[event.index]) _uiErrorMessages[event.index] = {};
				_uiErrorMessages[event.index][field] = errorObject;
				return _uiErrorMessages;
			});

			setIsEnabled(false);
			areUiErrors.current = true;

		} else {
			setUiErrorMessages((uiErrorMessages) => {
				const _uiErrorMessages = global.structuredClone(uiErrorMessages);
				if (_uiErrorMessages[event.index]) _uiErrorMessages[event.index][field] = null;
				return _uiErrorMessages;
			})
		}
	})
}

export function validateFormBioEntityFields(newAnnotationForm, uiErrorMessages,  setUiErrorMessages, areUiErrors) {
	const bioEntityFieldNames = ["subject", "sgdStrainBackground", "assertedAllele"];

	bioEntityFieldNames.forEach((field) => {
		if(newAnnotationForm[field] && !Object.keys(newAnnotationForm['subject']).includes("curie")){
			const _uiErrorMessages = {};
			_uiErrorMessages[field] = "Must select from autosuggest";
			setUiErrorMessages({..._uiErrorMessages});
			areUiErrors.current = true;
		}
	})
}

export const removeInvalidFilters = (currentFilters) => {
	const currentFiltersCopy = global.structuredClone(currentFilters);

	if (currentFiltersCopy && Object.keys(currentFiltersCopy).length > 0) {
		const invalidFilters = [];

		let validFilters = {};
		Object.entries(FIELD_SETS).forEach(([key, value]) => {
			validFilters[[FIELD_SETS[key].filterName]] = value;
		});

		for (let filterName in currentFiltersCopy) {
			if(validFilters[filterName]) {
				let validFields = validFilters[filterName].fields;
				const invalidFields = [];
				for(let fieldName in currentFiltersCopy[filterName]) {
					if(!validFields.includes(fieldName)) {
						invalidFields.push(fieldName);
					}
				}
				invalidFields.forEach(fieldName => {
					delete currentFiltersCopy[filterName][fieldName];
				});
				if(Object.keys(currentFiltersCopy[filterName]).length === 0) {
					delete currentFiltersCopy[filterName];
				}
			} else {
				invalidFilters.push(filterName);
			}
		}
		invalidFilters.forEach(filterName => {
			delete currentFiltersCopy[filterName];
		});
	}

	return currentFiltersCopy;
}

export const removeInvalidSorts = (currentSorts) => {
	const currentSortsCopy = global.structuredClone(currentSorts);

	let invalidSorts = [];
	if (!currentSortsCopy || currentSortsCopy.length === 0) {
		return currentSortsCopy;
	} else {
		currentSortsCopy.forEach((sort) => {
			if (!SORT_FIELDS.includes(sort.field)) invalidSorts.push(sort.field);
		})
		invalidSorts.forEach(field => {
			currentSortsCopy.splice(currentSortsCopy.findIndex((sort) => sort.field === field), 1);
		});
	}

	return currentSortsCopy;
}
