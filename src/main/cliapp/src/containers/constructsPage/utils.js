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
