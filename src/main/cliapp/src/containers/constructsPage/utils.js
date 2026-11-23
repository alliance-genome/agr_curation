import { getIdentifier } from '../../utils/utils';
import { getPendingFields, nameSlotAnnotationValue } from '../../utils/pendingFields';

// The value each of the construct's single-value fields and single-object sections is compared by, with an unset
// value read as none.
const CONSTRUCT_FIELD_VALUES = {
	constructSymbol: (construct) => nameSlotAnnotationValue(construct?.constructSymbol),
	constructFullName: (construct) => nameSlotAnnotationValue(construct?.constructFullName),
	placeholder: (construct) => construct?.placeholder ?? null,
	internal: (construct) => construct?.internal ?? null,
	obsolete: (construct) => construct?.obsolete ?? null,
};

/**
 * The construct's single-value fields and single-object sections whose value on the form differs from the construct
 * as last saved.
 *
 * @param {Object} construct the construct as the form holds it
 * @param {Object} [savedConstruct] the construct as the API last returned it
 * @returns {Set<string>} the pending fields' names, none while there is no saved construct
 */
export const getPendingConstructFields = (construct, savedConstruct) =>
	getPendingFields(construct, savedConstruct, CONSTRUCT_FIELD_VALUES);

// The cassette's symbol, or its identifier when it has none: the text of the Cassettes column.
export const cassetteLabel = (cassette) => cassette?.cassetteSymbol?.displayText || getIdentifier(cassette) || '';

export const cassetteDisplayValue = (cassette) => {
	const symbol = cassette?.cassetteSymbol?.displayText;
	const identifier = getIdentifier(cassette) || '';
	return symbol && symbol !== identifier ? `${symbol} (${identifier})` : identifier;
};

// Restricts a cassette search to the cassettes of the given construct cassette associations.
export const constructCassettesFilter = (constructCassetteAssociations) => {
	const ids = (constructCassetteAssociations || [])
		.map((association) => association?.constructCassetteAssociationObject?.id)
		.filter(Boolean);
	return ids.length > 0
		? { constructCassettesFilter: { id: { queryString: ids.join(' '), tokenOperator: 'OR' } } }
		: null;
};
