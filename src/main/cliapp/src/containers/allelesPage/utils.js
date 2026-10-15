export const generateCrossRefSearchFields = (references) => {
	if (references) {
		references.forEach((reference) => {
			reference.crossReferencesFilter = generateCrossRefSearchField(reference);
		});
	}
};

export const generateCrossRefSearchField = (reference) => {
	const { crossReferences, curieField } = getCrossReferences(reference);

	let refStrings = crossReferences.map((crossRef) => crossRef[curieField]);

	return refStrings.join();
};

export const getCrossReferences = (reference) => {
	let crossReferences;
	let curieField;

	if (reference.cross_references) {
		crossReferences = structuredClone(reference.cross_references);
		curieField = 'curie';
	} else if (reference.crossReferences) {
		crossReferences = structuredClone(reference.crossReferences);
		curieField = 'referencedCurie';
	} else {
		return {};
	}

	return { crossReferences, curieField };
};

export const getShortCitation = (reference) => {
	let shortCitation;
	if (!reference.short_citation && !reference.shortCitation) return;

	if (reference.short_citation) {
		shortCitation = reference.short_citation;
	} else if (reference.shortCitation) {
		shortCitation = reference.shortCitation;
	}

	return shortCitation;
};

export const generateCurieSearchField = (entities) => {
	if (!entities) return;
	let curieStrings = entities.map((entity) => entity.curie);
	return curieStrings.join();
};

export const generateCurieSearchFields = (entities, subArrayField) => {
	if (!entities) return;
	entities.forEach((entity) => {
		entity.evidenceCurieSearchFilter = generateCurieSearchField(entity[subArrayField]);
	});
};

export const validateRequiredAutosuggestField = (table, errorMessages, dispatch, entityType, fieldName) => {
	let areUiErrors = false;
	const newErrorMessages = structuredClone(errorMessages);

	for (let i = 0; i < table.length; i++) {
		const row = table[i];
		const fieldValue = row[fieldName];
		if (!fieldValue || typeof fieldValue === 'string') {
			const errorMessage = {
				...newErrorMessages[row.dataKey],
				[fieldName]: { message: `Must select ${fieldName} from autosuggest`, severity: 'error' },
			};
			newErrorMessages[row.dataKey] = errorMessage;
			areUiErrors = true;
		}
	}

	if (areUiErrors) {
		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: entityType,
			errorMessages: newErrorMessages,
		});
	}

	return areUiErrors;
};

export const addDataKey = (entity) => {
	entity.dataKey = crypto.randomUUID();
};

/**
 * An empty allele symbol, ready to be edited in the symbol table.
 *
 * @returns {Object} a symbol with blank text fields and no name type
 */
export const buildEmptyAlleleSymbol = () => ({
	dataKey: 0,
	synonymUrl: '',
	internal: false,
	nameType: null,
	formatText: '',
	displayText: '',
});

/**
 * Shapes a new allele for the create endpoint.
 *
 * Adds the `type` discriminator BiologicalEntity declares through `@JsonTypeInfo`, which an
 * allele loaded from the API already carries but a new one does not, and strips the placeholder
 * objects the initial state holds: the API reads `{ name: '' }` as a vocabulary term whose name
 * it cannot find and rejects it, and `{ curie: '' }` as a taxon it silently resolves to null.
 *
 * @param {Object} allele
 * @returns {Object} a copy carrying `type`, without a blank `taxon` or `inCollection`, and
 *   without the association subjects the API assigns
 */
export const buildCreatePayload = (allele) => {
	const payload = structuredClone(allele);

	payload.type = 'Allele';

	if (!payload.taxon?.curie) {
		delete payload.taxon;
	}
	if (!payload.inCollection?.name) {
		delete payload.inCollection;
	}

	// The API sets each association's subject to the allele it persists, so the copy the form holds
	// is never read. It also has to go: a nested allele carries no `type` discriminator, and Jackson
	// rejects the whole payload over it before the request reaches validation.
	payload.alleleGeneAssociations?.forEach((association) => {
		delete association.alleleAssociationSubject;
	});

	return payload;
};

// The fields a stored row carries that belong to it alone, and so cannot be carried onto a copy.
const ROW_IDENTITY_FIELDS = [
	'id',
	'createdBy',
	'updatedBy',
	'dateCreated',
	'dateUpdated',
	'dbDateCreated',
	'dbDateUpdated',
];

const copyRow = (row) => {
	if (!row) return null;
	const copy = structuredClone(row);
	ROW_IDENTITY_FIELDS.forEach((field) => delete copy[field]);
	return copy;
};

const copyRows = (rows) => (rows?.length ? rows.map(copyRow) : undefined);

/**
 * A new allele carrying the fields of a stored one that the create form edits, for the create page to
 * start from.
 *
 * Left behind: everything that identifies the stored allele (its ids and secondary IDs), its data
 * provider and audit fields, and the variant and construct associations the form does not edit. Cross
 * references are written through their own sub-resource and copied separately; see
 * buildDuplicateCrossReferences. Each copied row, and a gene association's note, loses its own id and audit
 * fields, so it is created afresh. A list with no rows is left unset, as the detail endpoint leaves
 * it, so its section starts hidden.
 *
 * @param {Object} allele an allele as the detail endpoint returns it
 * @returns {Object} an allele without an id, not obsolete, carrying only the lists that have rows
 */
export const buildDuplicateAllele = (allele) => {
	const alleleGeneAssociations = copyRows(allele.alleleGeneAssociations);
	alleleGeneAssociations?.forEach((association) => {
		delete association.alleleAssociationSubject;
		if (association.relatedNote) {
			association.relatedNote = copyRow(association.relatedNote);
		}
	});

	return {
		type: 'Allele',
		taxon: structuredClone(allele.taxon) ?? { curie: '' },
		inCollection: structuredClone(allele.inCollection) ?? { name: '' },
		isExtinct: allele.isExtinct ?? false,
		internal: allele.internal ?? false,
		obsolete: false,
		references: allele.references?.length ? structuredClone(allele.references) : undefined,
		relatedNotes: copyRows(allele.relatedNotes),
		alleleSymbol: copyRow(allele.alleleSymbol),
		alleleFullName: copyRow(allele.alleleFullName),
		alleleSynonyms: copyRows(allele.alleleSynonyms),
		alleleMutationTypes: copyRows(allele.alleleMutationTypes),
		alleleInheritanceModes: copyRows(allele.alleleInheritanceModes),
		alleleFunctionalImpacts: copyRows(allele.alleleFunctionalImpacts),
		alleleNomenclatureEvents: copyRows(allele.alleleNomenclatureEvents),
		alleleGermlineTransmissionStatus: copyRow(allele.alleleGermlineTransmissionStatus),
		alleleDatabaseStatus: copyRow(allele.alleleDatabaseStatus),
		alleleGeneAssociations,
	};
};

const evidenceCuries = (slotAnnotation) =>
	Array.isArray(slotAnnotation.evidence) ? slotAnnotation.evidence.map((reference) => reference?.curie).sort() : [];

// A single-object section's curator-editable fields, as a string to compare by, or none when it is unset.
const nameSlotAnnotationValue = (nameSlotAnnotation) =>
	nameSlotAnnotation
		? JSON.stringify([
				nameSlotAnnotation.displayText || null,
				nameSlotAnnotation.formatText || null,
				nameSlotAnnotation.synonymUrl || null,
				nameSlotAnnotation.nameType?.name ?? null,
				nameSlotAnnotation.synonymScope?.name ?? null,
				nameSlotAnnotation.internal ?? null,
				evidenceCuries(nameSlotAnnotation),
			])
		: null;

const statusSlotAnnotationValue = (statusSlotAnnotation, statusField) =>
	statusSlotAnnotation
		? JSON.stringify([
				statusSlotAnnotation[statusField]?.name ?? null,
				statusSlotAnnotation.internal ?? null,
				evidenceCuries(statusSlotAnnotation),
			])
		: null;

// The value each single-value field and single-object section is compared by, with an unset or blank
// value read as none.
const SINGLE_VALUE_FIELD_VALUES = {
	taxon: (allele) => allele?.taxon?.curie || null,
	inCollection: (allele) => allele?.inCollection?.name || null,
	isExtinct: (allele) => allele?.isExtinct ?? null,
	internal: (allele) => allele?.internal ?? null,
	obsolete: (allele) => allele?.obsolete ?? null,
	alleleFullName: (allele) => nameSlotAnnotationValue(allele?.alleleFullName),
	alleleSymbol: (allele) => nameSlotAnnotationValue(allele?.alleleSymbol),
	alleleGermlineTransmissionStatus: (allele) =>
		statusSlotAnnotationValue(allele?.alleleGermlineTransmissionStatus, 'germlineTransmissionStatus'),
	alleleDatabaseStatus: (allele) => statusSlotAnnotationValue(allele?.alleleDatabaseStatus, 'databaseStatus'),
};

export const SINGLE_VALUE_FIELDS = Object.keys(SINGLE_VALUE_FIELD_VALUES);

/**
 * The single-value fields and single-object sections whose value on the form differs from the allele as
 * last saved.
 *
 * @param {Object} allele the allele as the form holds it
 * @param {Object} [savedAllele] the allele as the API last returned it
 * @returns {Set<string>} the pending fields' names, none while there is no saved allele
 */
export const getPendingSingleValueFields = (allele, savedAllele) => {
	if (!savedAllele) return new Set();
	return new Set(
		SINGLE_VALUE_FIELDS.filter(
			(field) => SINGLE_VALUE_FIELD_VALUES[field](allele) !== SINGLE_VALUE_FIELD_VALUES[field](savedAllele)
		)
	);
};

export const processErrors = (data, dispatch, allele) => {
	const errorMap = data?.supplementalData?.errorMap;
	const errorMessages = data?.errorMessages;

	// Dispatched before the row level messages, which are keyed off the allele and can fail on an
	// unexpected shape. These are the only messages some fields carry, so they go out first.
	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages: errorMessages || {},
	});

	processErrorMap(errorMap, dispatch, allele);
};

export const processErrorMap = (errorMap, dispatch, allele) => {
	if (!errorMap) return;

	Object.keys(errorMap).forEach((entityType) => {
		const tableErrors = errorMap[entityType];
		const table = allele[entityType];
		// Only an error map keyed by row or by field can be attached to a row. A plain string, or
		// an entity the allele does not carry, stays in the flat error messages.
		if (!table || typeof table !== 'object' || !tableErrors || typeof tableErrors !== 'object') {
			return;
		}
		processTableErrors(tableErrors, dispatch, entityType, table);
	});
};

export const processTableErrors = (tableErrors, dispatch, entityType, table) => {
	let errors = {};
	Object.keys(tableErrors).forEach((index) => {
		let row = Array.isArray(table) ? table[index] : table;
		let rowErrors = Array.isArray(table) ? tableErrors[index] : tableErrors;
		errors[row.dataKey] = {};
		Object.keys(rowErrors).forEach((field) => {
			errors[row.dataKey][field] = {
				severity: 'error',
				message: rowErrors[field],
			};
		});
	});
	dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: entityType, errorMessages: errors });
};
