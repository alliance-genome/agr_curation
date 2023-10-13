export const generateCrossRefSearchFields = (references) => {
  references.forEach((reference) => {
    reference.crossReferencesFilter = generateCrossRefSearchField(reference);
  })
};

export const generateCrossRefSearchField = (reference) => {
  const { crossReferences, curieField } = differentiateCrossReferences(reference);

  let refStrings = crossReferences.map((crossRef) => crossRef[curieField]);

  return refStrings.join();


};

export const validateReferenceTable = (table, alleleDispatch, state) => {
  const errors = [];
  table.forEach((row, index) => {
    if (Object.keys(row).length <= 1) {
      errors[index] = {};
      errors[index].select = {
        severity: "error",
        message: "Must select reference from dropdown"
      };
      alleleDispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: state.field, errorMessages: errors });
    }
  });
  if (errors.length > 0) return true;
};

export const differentiateCrossReferences = (reference) => {
  let crossReferences;
  let curieField;

  if (reference.cross_references) {
    crossReferences = global.structuredClone(reference.cross_references);
    curieField = "curie";
  } else if (reference.crossReferences) {
    crossReferences = global.structuredClone(reference.crossReferences);
    curieField = "referencedCurie";
  } else {
    return {};
  }

  return { crossReferences, curieField };
};