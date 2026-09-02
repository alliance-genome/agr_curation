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
 * Shapes a new allele for the create endpoint.
 *
 * Adds the `type` discriminator BiologicalEntity declares through `@JsonTypeInfo`, which an
 * allele loaded from the API already carries but a new one does not, and strips the placeholder
 * objects the initial state holds: the API reads `{ name: '' }` as a vocabulary term whose name
 * it cannot find and rejects it, and `{ curie: '' }` as a taxon it silently resolves to null.
 *
 * @param {Object} allele
 * @returns {Object} a copy carrying `type` and without a blank `taxon` or `inCollection`
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

	return payload;
};

/**
 * Checks the fields the API requires to create an allele, dispatching any messages so they
 * render beside their field. The allele's identifier is the curie the API mints, so neither
 * MOD identifier is checked.
 *
 * @param {Object} allele
 * @param {Function} dispatch - allele reducer dispatch
 * @returns {boolean} whether any field is missing
 */
export const validateRequiredCreateFields = (allele, dispatch) => {
	const errorMessages = {};

	if (!allele.taxon?.curie) {
		errorMessages.taxon = 'Required';
	}

	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages,
	});

	return Object.keys(errorMessages).length > 0;
};

export const processErrors = (data, dispatch, allele) => {
	const errorMap = data?.supplementalData?.errorMap;
	const errorMessages = data?.errorMessages;

	processErrorMap(errorMap, dispatch, allele);

	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages: errorMessages || {},
	});
};

export const processErrorMap = (errorMap, dispatch, allele) => {
	if (!errorMap) return;

	let tableErrors;
	let table;
	Object.keys(errorMap).forEach((entityType) => {
		tableErrors = errorMap[entityType];
		table = allele[entityType];
		if (typeof table === 'object') {
			processTableErrors(tableErrors, dispatch, entityType, table);
		}
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
