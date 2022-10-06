import { confirmDialog } from 'primereact/confirmdialog';

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
		return selectedColumnNames.includes(col.header);
	})
	return filteredColumns;
};

export function orderColumns(columns, selectedColumnNames) {
	let orderedColumns = [];
	selectedColumnNames.forEach((columnName) => {
		orderedColumns.push(columns.filter(col => col.header === columnName)[0]);
	});
	return orderedColumns;
};

export function reorderArray(array, from, to) {
	const item = array.splice(from, 1);
	array.splice(to, 0, item[0]);
	return array;
};

export function setDefaultColumnOrder(columns, dataTable, defaultColumnOptions, deletionEnabled = false) {
	let initalColumnOrderObjects = [];
	let initalColumnOrderFields = [];

	defaultColumnOptions.forEach((option) => {
		initalColumnOrderObjects.push(
			columns.find((column) => {
				return column.header === option;
			})
		)
	});

	initalColumnOrderFields = initalColumnOrderObjects.map(column => column.field);

	if(deletionEnabled) {
		initalColumnOrderFields.unshift('delete');
	}

	initalColumnOrderFields.unshift('rowEditor');

	dataTable.current.state.columnOrder = initalColumnOrderFields
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
		referenceItem.crossReferences.forEach((x,i) => xrefCuries.push(x.curie));
	}
	
	if (xrefCuries.length === 1)
		return xrefCuries[0] + ' (' + referenceItem.curie + ')';
	let primaryXrefCurie = '';
	
	if (indexWithPrefix(xrefCuries, 'PMID:') > -1) { 
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'PMID:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'FB:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'FB:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'MGI:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'MGI:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'RGD:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'RGD:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'SGD:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'SGD:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'WB:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'WB:'), 1);
	} else if (indexWithPrefix(xrefCuries, 'ZFIN:') > -1) {
		primaryXrefCurie = xrefCuries.splice(indexWithPrefix(xrefCuries, 'ZFIN:'), 1);
	} else {
		primaryXrefCurie = xrefCuries.splice(0, 1);
	}
	
	return primaryXrefCurie + ' (' + xrefCuries.join('|') + '|' + referenceItem.curie + ')';
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

export function filterDropDownObject(inputValue, object){
	const trimmedValue = trimWhitespace(inputValue.toLowerCase());
	let _object = global.structuredClone(object);

	if (_object.synonyms?.length > 0) {
		const { synonyms } = _object;
		const filteredSynonyms = synonyms.filter((synonym) => {
			let selectedItem = synonym.name ? synonym.name.toString().toLowerCase() : synonym.toString().toLowerCase();
			return selectedItem.indexOf(trimmedValue) !== -1;
		});
		_object = { ..._object, synonyms: filteredSynonyms }
	}

	if (_object.crossReferences?.length > 0) {
		const { crossReferences } = _object;
		const filteredCrossReferences = crossReferences.filter((crossReference) => {
			return crossReference.curie.toString().toLowerCase().indexOf(trimmedValue) !== -1;
		});
		_object = { ..._object, crossReferences: filteredCrossReferences }
	}

	if (_object.cross_references?.length > 0) {
		const { cross_references } = _object;
		const filteredCrossReferences = cross_references.filter((cross_reference) => {
			return cross_reference.curie.toString().toLowerCase().indexOf(trimmedValue) !== -1;
		});
		_object = { ..._object, cross_references: filteredCrossReferences }
	}
	if (_object.secondaryIdentifiers?.length > 0) {
		const { secondaryIdentifiers } = _object;
		const filteredSecondaryIdentifiers = secondaryIdentifiers.filter((secondaryIdentifier) => {
			return secondaryIdentifier.toString().toLowerCase().indexOf(trimmedValue) !== -1;
		});
		_object = { ..._object, secondaryIdentifiers: filteredSecondaryIdentifiers }
	}

	return _object;
}

export function onSelectionOver(event, item, query, op, setAutocompleteSelectedItem) {
	const _item = filterDropDownObject(query, item)
	setAutocompleteSelectedItem(_item);
	op.current.show(event);
};
