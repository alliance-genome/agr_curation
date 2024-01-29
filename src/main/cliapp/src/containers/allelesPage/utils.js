export const generateCrossRefSearchFields = (references) => {
  if (references) {
    references.forEach((reference) => {
      reference.crossReferencesFilter = generateCrossRefSearchField(reference);
    });
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
  if (!entities) return;
  let curieStrings = entities.map((entity) => entity.curie);
  return curieStrings.join();
};

export const generateCurieSearchFields = (entities, subArrayField) => {
  if (!entities) return;
  entities.forEach((entity) => {
    entity.evidenceCurieSearchFilter = generateCurieSearchField(entity[subArrayField]);
  });
};

export const validateRequiredAutosuggestField = (table, errorMessages, dispatch, entityType, fieldName) => {
  let areUiErrors = false;
  const newErrorMessages = global.structuredClone(errorMessages);

  for (let i = 0; i < table.length; i++) {
    const row = table[i];
    const fieldValue = row[fieldName];
    if (!fieldValue || typeof fieldValue === "string") {
      const errorMessage = {
        ...newErrorMessages[row.dataKey],
        [fieldName]: { message: `Must select ${fieldName} from dropdown`, severity: "error" },
      };
      newErrorMessages[row.dataKey] = errorMessage;
      areUiErrors = true;
    }
  }

  if (areUiErrors) {
    dispatch({
      type: "UPDATE_TABLE_ERROR_MESSAGES",
      entityType: entityType,
      errorMessages: newErrorMessages,
    });
  }

  return areUiErrors;
};

export const addDataKey = (entity) => {
  entity.dataKey = global.crypto.randomUUID();
};

export const processErrors = (data, dispatch, allele) => {
  const errorMap = data?.supplementalData?.errorMap;
  const errorMessages = data?.errorMessages;

  processErrorMap(errorMap, dispatch, allele);

  dispatch(
    {
      type: "UPDATE_ERROR_MESSAGES",
      errorMessages: errorMessages || {}
    }
  );

};

export const processErrorMap = (errorMap, dispatch, allele) => {
  if (!errorMap) return;

  let tableErrors;
  let table;
  Object.keys(errorMap).forEach((entityType) => {
    tableErrors = errorMap[entityType];
    table = allele[entityType];
    if (typeof table === 'object') {
      processTableErrors(tableErrors, dispatch, entityType, table);
    }
  });
};

export const processTableErrors = (tableErrors, dispatch, entityType, table) => {
  let errors = {};
  Object.keys(tableErrors).forEach((index) => {
    let row = Array.isArray(table) ? table[index] : table;
    let rowErrors = Array.isArray(table) ? tableErrors[index] : tableErrors;
    errors[row.dataKey] = {};
    Object.keys(rowErrors).forEach((field) => {
      errors[row.dataKey][field] = {
        severity: "error",
        message: rowErrors[field]
      };
    });
  });
  dispatch({ type: "UPDATE_TABLE_ERROR_MESSAGES", entityType: entityType, errorMessages: errors });
};

