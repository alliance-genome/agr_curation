import { isPickedReference } from '../../components/Editors/autocomplete/references/utils';

const REQUIRED_MESSAGE = 'This is a required field';
const ALWAYS_REQUIRED_FIELDS = ['relation', 'diseaseAnnotationObject', 'evidenceItem', 'evidenceCodes'];

/**
 * Whether a field's value counts as empty: missing, an empty list or a blank string.
 *
 * @param {any} value - the field's value
 * @returns {boolean}
 */
const isEmptyValue = (value) => value == null || value === '' || (Array.isArray(value) && value.length === 0);

/**
 * The messages for the required fields a new annotation leaves empty: relation, disease, reference and evidence
 * codes, plus an experiment for ZFIN curators.
 *
 * @param {object} annotation - the annotation being created
 * @param {string[]} staffGroups - the curator's effective staff groups
 * @returns {object} messages keyed by field, empty when every required field is filled
 */
export const findMissingRequiredFields = (annotation, staffGroups) => {
	const missing = {};
	ALWAYS_REQUIRED_FIELDS.forEach((field) => {
		if (isEmptyValue(annotation[field])) missing[field] = REQUIRED_MESSAGE;
	});
	if (staffGroups?.includes('ZFINStaff') && !annotation.conditionRelations?.[0]?.handle) {
		missing.conditionRelations = REQUIRED_MESSAGE;
	}
	return missing;
};

/**
 * The messages for fields holding typed text rather than an entry picked from the suggestions: the subject and SGD
 * strain background must carry the `type` a search result has, and the reference must be a picked reference.
 *
 * @param {object} annotation - the annotation being created
 * @returns {object} messages keyed by field, empty when every filled field holds a picked entry
 */
export const findUnpickedFields = (annotation) => {
	const unpicked = {};
	['diseaseAnnotationSubject', 'sgdStrainBackground'].forEach((field) => {
		const entity = annotation[field];
		if (entity && !entity.type) unpicked[field] = 'Must select from autosuggest';
	});
	const reference = annotation.evidenceItem;
	if (reference?.curie && !isPickedReference(reference)) {
		unpicked.evidenceItem = 'Must select reference from dropdown';
	}
	return unpicked;
};
