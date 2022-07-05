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

export function getRefID(referenceItem) {
	if (!referenceItem)
		return ''
	let pmid = ''
	let pmodid = ''
	if(!referenceItem.cross_references) return;
	referenceItem.cross_references.forEach((entry) => {
		if (entry.curie.startsWith('PMID:')) {
			pmid = entry.curie;
		}
	})
	if (pmid === "") {
		referenceItem.cross_references.forEach((entry) => {
			if (entry.curie.startsWith('MGI:') ||
				entry.curie.startsWith('RGD:') ||
				entry.curie.startsWith('ZFIN:') ||
				entry.curie.startsWith('WB:') ||
				entry.curie.startsWith('SGD:') ||
				entry.curie.startsWith('FB:') ||
				entry.curie.startsWith('DOI:') ||
				entry.curie.startsWith('PMCID:')) {
				pmodid = entry.curie;
			}
		})
	}
	// use pmid if non-nul otherwise use pmodID
	return pmid ? pmid : pmodid;
}


export function getRefObject(referenceItem) {
	if (!referenceItem)
		return;
	const curationDbRef = {};
	let primaryXref = '';
	const secondaryXrefs = [];
	
	for (const xref of referenceItem.cross_references) {
		if (xref.curie.startsWith('PMID:')) {
			primaryXref = xref.curie;
		} else {
			secondaryXrefs.push(xref.curie);
		}
	}

	secondaryXrefs.sort();
	if (!primaryXref) {
		primaryXref = secondaryXrefs.shift();
	}

	curationDbRef.curie = referenceItem.curie;
	curationDbRef.primaryCrossReference = primaryXref;
	curationDbRef.submittedCrossReference = primaryXref;
	if (secondaryXrefs && secondaryXrefs.length > 0)
		curationDbRef.secondaryCrossReferences = secondaryXrefs.slice(0);
	
	return curationDbRef;
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
