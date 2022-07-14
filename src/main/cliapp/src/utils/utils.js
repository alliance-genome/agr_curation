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

export function setDefaultColumnOrder(columns, dataTable, defaultColumnOptions) {
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
