import { getIdentifier } from '../../utils/utils';

export const generateCrossRefSearchFields = (references) => {
	if (references) {
		references.forEach((reference) => {
			reference.crossReferencesFilter = generateCrossRefSearchField(reference);
		});
	}
};

export const generateCrossRefSearchField = (reference) => {
	const crossReferences = reference.crossReferences || [];

	let refStrings = crossReferences.map((crossRef) => crossRef.referencedCurie);

	return refStrings.join();
};

export const generateComponentSearchFields = (associations) => {
	if (associations) {
		associations.forEach((association) => {
			association.componentSearchField = generateComponentSearchField(association);
		});
	}
};

export const generateComponentSearchField = (association) => {
	const obj = association?.constructGenomicEntityAssociationObject;
	if (!obj) return '';

	const searchParts = [
		obj.primaryExternalId,
		obj.alleleSymbol?.displayText,
		obj.geneSymbol?.displayText,
		obj.name,
	].filter(Boolean);

	return searchParts.join(' ');
};

// The cassette's symbol, or its identifier when it has none: the text of the Cassettes column.
export const cassetteLabel = (cassette) => cassette?.cassetteSymbol?.displayText || getIdentifier(cassette) || '';

// Restricts a cassette search to the cassettes of the given construct cassette associations.
export const constructCassettesFilter = (constructCassetteAssociations) => {
	const ids = (constructCassetteAssociations || [])
		.map((association) => association?.constructCassetteAssociationObject?.id)
		.filter(Boolean);
	return ids.length > 0
		? { constructCassettesFilter: { id: { queryString: ids.join(' '), tokenOperator: 'OR' } } }
		: null;
};
