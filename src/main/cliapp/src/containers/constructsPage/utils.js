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
