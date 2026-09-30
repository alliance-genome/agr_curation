import { addDataKey } from '../utils';
import { ResourceDescriptorService } from '../../../service/ResourceDescriptorService';

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
 * The resource descriptor prefix a curie names: everything before the first colon, the same rule the API
 * applies. A curie with no colon, or one that starts with it, names none.
 *
 * @param {string} referencedCurie
 * @returns {string|null}
 */
export const curiePrefixOf = (referencedCurie) => {
	const separatorIndex = typeof referencedCurie === 'string' ? referencedCurie.indexOf(':') : -1;

	return separatorIndex > 0 ? referencedCurie.slice(0, separatorIndex) : null;
};

/**
 * Lifts the descriptor a stored page belongs to onto the row itself, where the descriptor column
 * reads it.
 *
 * The descriptor arriving this way carries no `resourcePages`: a cross reference is serialized
 * without them, so the page dropdown has nothing to offer until they are fetched. Use
 * `seedResourceDescriptors` to load rows for editing; this is the shape it builds on.
 *
 * @param {Object} crossReference
 * @returns {Object} a copy carrying `resourceDescriptor`; the argument is not modified
 */
export const seedResourceDescriptor = (crossReference) => ({
	...crossReference,
	resourceDescriptor: crossReference.resourceDescriptorPage?.resourceDescriptor ?? null,
});

/**
 * Seeds each row's descriptor and replaces it with the fully loaded one, so the page dropdown can
 * offer that descriptor's other pages straight away rather than only after the curator re-picks it.
 *
 * Each distinct descriptor is read once however many rows share it. A descriptor that cannot be read
 * keeps the one nested in the cross reference, which still names the descriptor and only costs that
 * row its page choices - a failure here must not stop the rows loading.
 *
 * @param {Array<Object>} crossReferences
 * @returns {Promise<Array<Object>>} the rows, each carrying `resourceDescriptor`
 */
export const seedResourceDescriptors = async (crossReferences) => {
	const seededRows = (crossReferences ?? []).map(seedResourceDescriptor);

	const descriptorIds = [...new Set(seededRows.map((row) => row.resourceDescriptor?.id).filter((id) => id != null))];
	if (descriptorIds.length === 0) return seededRows;

	const resourceDescriptorService = new ResourceDescriptorService();
	const loadedDescriptors = new Map();

	await Promise.all(
		descriptorIds.map(async (descriptorId) => {
			try {
				const response = await resourceDescriptorService.getResourceDescriptor(descriptorId);
				const descriptor = response?.data?.entity;
				if (descriptor) loadedDescriptors.set(descriptorId, descriptor);
			} catch (error) {
				console.warn(`Could not load resource descriptor ${descriptorId}`, error);
			}
		})
	);

	return seededRows.map((row) => {
		const loadedDescriptor = loadedDescriptors.get(row.resourceDescriptor?.id);
		return loadedDescriptor ? { ...row, resourceDescriptor: loadedDescriptor } : row;
	});
};

const DEFAULT_PAGE_NAME = 'default';

// The curie a descriptor starts a row with: its prefix and the colon, ready for the local id.
const curiePrefixFor = (resourceDescriptor) => (resourceDescriptor?.prefix ? `${resourceDescriptor.prefix}:` : '');

/**
 * Applies one field edit, keeping the page and curie consistent with the descriptor.
 *
 * Choosing a descriptor keeps the current page when that page is one of the descriptor's
 * `resourcePages`, matched by id. Otherwise the page becomes the descriptor's page named `default`, or
 * none when it has no such page, so a page cannot be submitted against a descriptor it does not belong
 * to. A descriptor typed rather than chosen carries no `resourcePages` and therefore leaves no page.
 *
 * Choosing or clearing a descriptor also sets the curie to that descriptor's prefix, or empties it,
 * but only when the curie holds nothing the curator typed: it is empty or absent, or holds just the
 * prefix the previous descriptor left. A curie the curator typed is never changed.
 *
 * @param {Object} crossReference
 * @param {string} field
 * @param {*} value
 * @returns {Object} a copy carrying the edit; the argument is not modified
 */
export const applyCrossReferenceFieldChange = (crossReference, field, value) => {
	const updated = { ...crossReference, [field]: value };

	if (field === 'resourceDescriptor') {
		const pages = value?.resourcePages ?? [];
		const currentPageId = crossReference.resourceDescriptorPage?.id;
		const keepsPage = currentPageId != null && pages.some((page) => page.id === currentPageId);

		updated.resourceDescriptorPage = keepsPage
			? crossReference.resourceDescriptorPage
			: pages.find((page) => page.name === DEFAULT_PAGE_NAME) ?? null;

		const currentCurie = crossReference.referencedCurie ?? '';
		if (currentCurie === '' || currentCurie === curiePrefixFor(crossReference.resourceDescriptor)) {
			updated.referencedCurie = curiePrefixFor(value);
		}
	}

	return updated;
};

const REQUIRED_FIELDS = ['displayName', 'referencedCurie', 'resourceDescriptor', 'resourceDescriptorPage'];

export const MISSING_REQUIRED_FIELDS_MESSAGE = 'Some cross references are missing required fields';

const isEmptyValue = (value) => (typeof value === 'string' ? value.trim() === '' : value == null);

// A curie holding only its prefix, as choosing a descriptor leaves it, names nothing yet.
const lacksIdentifier = (curie) =>
	typeof curie === 'string' && curie.includes(':') && curie.slice(curie.indexOf(':') + 1).trim() === '';

/**
 * The required fields each row leaves empty, as the per-row error messages the table renders, keyed by
 * the row's `dataKey`. A row with every required field filled is left out. A curie that holds only its
 * prefix counts as missing its identifier, reported in the API's words.
 *
 * The resource descriptor is not stored on a cross reference, so the API never reports it missing; this
 * is the check that marks it.
 *
 * @param {Array<Object>} crossReferences
 * @returns {Object} per-row error messages; empty when nothing is missing
 */
export const findMissingRequiredFields = (crossReferences) => {
	const errorMessages = {};

	(crossReferences ?? []).forEach((crossReference) => {
		const rowErrors = Object.fromEntries(
			REQUIRED_FIELDS.filter((field) => isEmptyValue(crossReference[field])).map((field) => [
				field,
				{ severity: 'error', message: 'Required field is empty' },
			])
		);

		if (!rowErrors.referencedCurie && lacksIdentifier(crossReference.referencedCurie)) {
			rowErrors.referencedCurie = { severity: 'error', message: 'Identifier after the prefix is missing' };
		}

		if (Object.keys(rowErrors).length > 0) errorMessages[crossReference.dataKey] = rowErrors;
	});

	return errorMessages;
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

/**
 * Drops the table's own fields and the audit fields, leaving what the validate endpoint checks.
 *
 * The validate endpoint stores the person `createdBy` or `updatedBy` names when it does not already
 * know them, so a check that should write nothing sends neither. The audit dates go with them, since
 * only the API sets those.
 *
 * @param {Object} crossReference
 * @returns {Object} a copy without the table's own fields or the audit fields
 */
export const stripForValidation = (crossReference) => {
	/* eslint-disable-next-line no-unused-vars */
	const { createdBy, updatedBy, dateCreated, dateUpdated, ...checkedFields } = stripUiFields(crossReference);

	return checkedFields;
};
