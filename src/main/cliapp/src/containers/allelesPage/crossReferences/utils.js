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

// The fields a stored cross reference carries that belong to it alone, and so cannot be carried onto a copy.
const DUPLICATE_DROPPED_FIELDS = [
	'id',
	'createdBy',
	'updatedBy',
	'dateCreated',
	'dateUpdated',
	'dbDateCreated',
	'dbDateUpdated',
];

/**
 * Copies of an allele's cross references for a new allele. Each keeps its page, and so its resource
 * descriptor, and its internal flag, but not the curie and display name that identify the copied allele,
 * nor its id or audit fields. Every copy starts out not obsolete.
 *
 * @param {Object[]} [crossReferences] cross references as the API returns them
 * @returns {Object[]} rows with a blank curie and display name, each with its own `dataKey`
 */
export const buildDuplicateCrossReferences = (crossReferences) =>
	(crossReferences ?? []).map((crossReference) => {
		const copy = structuredClone(crossReference);
		DUPLICATE_DROPPED_FIELDS.forEach((field) => delete copy[field]);
		copy.referencedCurie = '';
		copy.displayName = '';
		copy.obsolete = false;
		addDataKey(copy);
		return copy;
	});

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

// A row's identity and curator-editable fields, as a string to compare by, with a blank value read as none.
const crossReferenceValue = (crossReference) =>
	JSON.stringify([
		crossReference.id ?? null,
		crossReference.referencedCurie || null,
		crossReference.displayName || null,
		crossReference.resourceDescriptor?.id ?? null,
		crossReference.resourceDescriptorPage?.id ?? null,
		crossReference.internal ?? null,
		crossReference.obsolete ?? null,
	]);

/**
 * Whether two lists of cross references hold the same rows, in the same order, ignoring the fields the
 * table adds.
 *
 * @param {Object[]} crossReferences
 * @param {Object[]} otherCrossReferences
 * @returns {boolean}
 */
export const haveSameCrossReferences = (crossReferences, otherCrossReferences) =>
	crossReferences.length === otherCrossReferences.length &&
	crossReferences.every(
		(crossReference, index) => crossReferenceValue(crossReference) === crossReferenceValue(otherCrossReferences[index])
	);

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
