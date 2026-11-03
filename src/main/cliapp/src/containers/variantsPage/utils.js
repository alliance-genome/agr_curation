import { copyRows } from '../../utils/copyRows';

export const addDataKey = (entity) => {
	entity.dataKey = crypto.randomUUID();
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

export const generateCrossRefSearchField = (reference) => {
	const { crossReferences, curieField } = getCrossReferences(reference);

	if (!crossReferences) return '';

	return crossReferences.map((crossRef) => crossRef[curieField]).join();
};

export const generateCrossRefSearchFields = (references) => {
	if (!references) return;
	references.forEach((reference) => {
		reference.crossReferencesFilter = generateCrossRefSearchField(reference);
	});
};

export const processErrors = (data, dispatch, variant) => {
	const errorMap = data?.supplementalData?.errorMap;
	const errorMessages = data?.errorMessages;

	// Dispatched before the row level messages, which are keyed off the variant and can fail on an
	// unexpected shape. These are the only messages some fields carry, so they go out first.
	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages: errorMessages || {},
	});

	processErrorMap(errorMap, dispatch, variant);
};

export const processErrorMap = (errorMap, dispatch, variant) => {
	if (!errorMap) return;

	Object.keys(errorMap).forEach((entityType) => {
		const tableErrors = errorMap[entityType];
		const table = variant[entityType];
		// Only an error map keyed by row or by field can be attached to a row. A plain string, or
		// an entity the variant does not carry, stays in the flat error messages.
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
		if (!row) return;
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

const addReferenceType = (references) => {
	references?.forEach((reference) => {
		if (reference && !reference.type) reference.type = 'Reference';
	});
};

export const buildSavePayload = (variant) => {
	const payload = structuredClone(variant);

	addReferenceType(payload.references);
	payload.relatedNotes?.forEach((note) => addReferenceType(note.references));

	return payload;
};

/**
 * A new variant carrying the fields of a stored one that the create form edits, for the create page
 * to start from.
 *
 * Left behind: everything that identifies the stored variant (its ids), its data provider, cross
 * references and audit fields - none of which the create form edits. Each copied related note loses
 * its own id and audit fields, so it is created afresh. References are shared records, so they are
 * kept as they are.
 *
 * @param {Object} variant a variant as the API returns it
 * @returns {Object} a variant without an id, not obsolete, carrying only the fields the create form edits
 */
export const buildDuplicateVariant = (variant) => ({
	type: 'Variant',
	taxon: structuredClone(variant.taxon) ?? { curie: '' },
	variantType: structuredClone(variant.variantType) ?? null,
	variantStatus: structuredClone(variant.variantStatus) ?? null,
	sourceGeneralConsequence: structuredClone(variant.sourceGeneralConsequence) ?? null,
	internal: variant.internal ?? false,
	obsolete: false,
	relatedNotes: copyRows(variant.relatedNotes),
	references: structuredClone(variant.references) ?? [],
	synonyms: structuredClone(variant.synonyms) ?? [],
});

export const buildCreatePayload = (variant) => {
	const payload = buildSavePayload(variant);

	payload.type = 'Variant';

	if (!payload.taxon?.curie) {
		delete payload.taxon;
	}

	return payload;
};
