import { sortedCuries } from './rows';

/**
 * A name slot annotation's curator-editable fields, as a string to compare by.
 *
 * @param {Object} [nameSlotAnnotation]
 * @returns {string|null} the fields, or none when the name is unset
 */
export const nameSlotAnnotationValue = (nameSlotAnnotation) =>
	nameSlotAnnotation
		? JSON.stringify([
				nameSlotAnnotation.displayText || null,
				nameSlotAnnotation.formatText || null,
				nameSlotAnnotation.synonymUrl || null,
				nameSlotAnnotation.nameType?.name ?? null,
				nameSlotAnnotation.synonymScope?.name ?? null,
				nameSlotAnnotation.internal ?? null,
				sortedCuries(nameSlotAnnotation.evidence),
			])
		: null;

/**
 * The fields of `fieldValues` whose value on the form differs from the entity as last saved.
 *
 * @param {Object} entity the entity as the form holds it
 * @param {Object} [savedEntity] the entity as the API last returned it
 * @param {Object<string, (entity: Object) => *>} fieldValues for each field, the value it is compared by, with an
 *   unset or blank value read as none
 * @returns {Set<string>} the pending fields' names, none while there is no saved entity
 */
export const getPendingFields = (entity, savedEntity, fieldValues) => {
	if (!savedEntity) return new Set();
	return new Set(
		Object.keys(fieldValues).filter((field) => fieldValues[field](entity) !== fieldValues[field](savedEntity))
	);
};
