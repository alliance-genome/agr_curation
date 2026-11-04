export const processErrors = (data, dispatch, entity) => {
	const errorMap = data?.supplementalData?.errorMap;
	const errorMessages = data?.errorMessages;

	// Dispatched before the row level messages, which are keyed off the entity and can fail on an
	// unexpected shape. These are the only messages some fields carry, so they go out first.
	dispatch({
		type: 'UPDATE_ERROR_MESSAGES',
		errorMessages: errorMessages || {},
	});

	processErrorMap(errorMap, dispatch, entity);
};

export const processErrorMap = (errorMap, dispatch, entity) => {
	if (!errorMap) return;

	Object.keys(errorMap).forEach((entityType) => {
		const tableErrors = errorMap[entityType];
		const table = entity[entityType];
		// Only an error map keyed by row or by field can be attached to a row. A plain string, or
		// a field the entity does not carry, stays in the flat error messages.
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
