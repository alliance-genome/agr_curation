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
		
	let xrefs = referenceItem.cross_references ? referenceItem.cross_references : referenceItem.crossReferences;
		
	if (xrefs.length === 1)
		return xrefs[0].curie + ' (' + referenceItem.curie + ')';
	
	let primaryXref = '';
	
	const secondaryXrefs = [];
	xrefs.forEach((x, i) => secondaryXrefs.push(x.curie));
	
	if (secondaryXrefs.indexOf('PMID:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('PMID:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('PMID:'), 1);
	} else if (secondaryXrefs.indexOf('FB:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('FB:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('FB:'), 1);
	} else if (secondaryXrefs.indexOf('MGI:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('MGI:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('MGI:'), 1);
	} else if (secondaryXrefs.indexOf('RGD:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('RGD:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('RGD:'), 1);
	} else if (secondaryXrefs.indexOf('SGD:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('SGD:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('SGD:'), 1);
	} else if (secondaryXrefs.indexOf('WB:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('WB:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('WB:'), 1);
	} else if (secondaryXrefs.indexOf('ZFIN:') > -1) {
		primaryXref = secondaryXrefs[secondaryXrefs.indexOf('ZFIN:')];
		secondaryXrefs.splice(secondaryXrefs.indexOf('ZFIN:'), 1);
	} else {
		primaryXref = secondaryXrefs[0];
		secondaryXrefs.splice(0, 1);
	}
	
	return primaryXref + ' (' + secondaryXrefs.join('|') + '|' + referenceItem.curie + ')';
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
