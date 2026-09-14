import { addDataKey } from '../utils';

/**
 * An empty cross reference, ready to be edited in the cross references table.
 *
 * @returns {Object} a cross reference with blank fields and its own `dataKey`
 */
export const buildNewCrossReference = () => {
	const crossReference = {
		referencedCurie: '',
		displayName: '',
		resourceDescriptor: null,
		resourceDescriptorPage: null,
		internal: false,
		obsolete: false,
	};

	addDataKey(crossReference);

	return crossReference;
};

/**
 * The resource descriptor prefix a curie names, by the rule CrossReference.getPrefix() applies on
 * the API: everything before the first colon, or the whole curie when it holds none.
 *
 * The first colon rather than the last, because a curie can carry more of them - a stored example
 * is `DOI:10.1016/s0896-6273(04)00073-x`.
 *
 * @param {string} referencedCurie
 * @returns {string} the prefix, or '' when there is no curie to read one from
 */
export const derivePrefix = (referencedCurie) => {
	if (!referencedCurie) return '';

	const separatorIndex = referencedCurie.indexOf(':');

	return separatorIndex === -1 ? referencedCurie : referencedCurie.slice(0, separatorIndex);
};

/**
 * Lifts the descriptor a stored page belongs to onto the row itself, where the descriptor column
 * reads it.
 *
 * The descriptor arriving this way carries no `resourcePages`: those are on the resource descriptor
 * views, and a cross reference is serialized without them. Its pages become available once a
 * descriptor is chosen from the autosuggest, which reads a view that does carry them.
 *
 * @param {Object} crossReference
 * @returns {Object} a copy carrying `resourceDescriptor`; the argument is not modified
 */
export const seedResourceDescriptor = (crossReference) => ({
	...crossReference,
	resourceDescriptor: crossReference.resourceDescriptorPage?.resourceDescriptor ?? null,
});

/**
 * Applies one field edit, keeping the page consistent with the descriptor.
 *
 * Choosing a descriptor keeps the current page when that page is one of the descriptor's
 * `resourcePages`, matched by id, and clears it otherwise - so a page cannot be submitted against a
 * descriptor it does not belong to. A descriptor typed rather than chosen carries no `resourcePages`
 * and therefore always clears the page.
 *
 * @param {Object} crossReference
 * @param {string} field
 * @param {*} value
 * @returns {Object} a copy carrying the edit; the argument is not modified
 */
export const applyCrossReferenceFieldChange = (crossReference, field, value) => {
	const updated = { ...crossReference, [field]: value };

	if (field === 'resourceDescriptor') {
		const currentPageId = crossReference.resourceDescriptorPage?.id;
		const keepsPage = currentPageId != null && (value?.resourcePages ?? []).some((page) => page.id === currentPageId);

		updated.resourceDescriptorPage = keepsPage ? crossReference.resourceDescriptorPage : null;
	}

	return updated;
};

/**
 * The row carrying a given key.
 *
 * Rows are looked up by key rather than read from an editor's `rowData`, which PrimeReact snapshots
 * when a row enters edit mode and does not refresh while that row stays open.
 *
 * @param {Array<Object>} rows
 * @param {string} dataKey
 * @returns {Object|undefined} the matching row, or undefined when there is none
 */
export const findRow = (rows, dataKey) => rows?.find((row) => row.dataKey === dataKey);

/**
 * Drops the fields the table keeps for itself, leaving what the API reads.
 *
 * `dataKey` is the table's row identity and `resourceDescriptor` scopes the page dropdown; neither
 * is a CrossReference field. The API ignores properties it does not declare, so this is about what
 * gets sent rather than whether it is accepted: a descriptor chosen from the autosuggest carries its
 * whole `resourcePages` list, which would otherwise ride along on every write.
 *
 * @param {Object} crossReference
 * @returns {Object} a copy without the table's own fields
 */
export const stripUiFields = (crossReference) => {
	/* eslint-disable-next-line no-unused-vars */
	const { dataKey, resourceDescriptor, ...apiFields } = crossReference;

	return apiFields;
};
