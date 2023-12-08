export const generateCrossRefSearchFields = (references) => {
  if(references) {
    references.forEach((reference) => {
      reference.crossReferencesFilter = generateCrossRefSearchField(reference);
    })
  }
};

export const generateCrossRefSearchField = (reference) => {
  const { crossReferences, curieField } = differentiateCrossReferences(reference);

  let refStrings = crossReferences.map((crossRef) => crossRef[curieField]);

  return refStrings.join();
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

export const generateCurieSearchField = (entities) => {
  if(!entities) return;
  let curieStrings = entities.map((entity) => entity.curie);
  return curieStrings.join();
};

export const generateCurieSearchFields = (entities, subArrayField) => {
  if(!entities) return;
  entities.forEach((entity) => {
    entity.curieSearchFilter = generateCurieSearchField(entity[subArrayField]);
  });
};