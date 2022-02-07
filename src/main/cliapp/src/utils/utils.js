export function returnSorted(event, originalSort) {

  let found = false;
  let replace = false;
  let newSort = [...originalSort];

  // console.log(event);
  // console.log(newSort);
  if (event.multiSortMeta.length > 0) {
    newSort.forEach((o) => {
      if (o.field === event.multiSortMeta[0].field) {
        if (o.order === event.multiSortMeta[0].order) {
          replace = true;
          found = true;
        } else {
          o.order = event.multiSortMeta[0].order;
          found = true;
        }
      }
    });
  } else {
    newSort = [];
  }

  if (!found) {
    return newSort.concat(event.multiSortMeta);
  } else {
    if (replace) {
      return event.multiSortMeta;
    } else {
      return newSort;
    }
  }
};

export function trimWhitespace(value) {
  return value.replace(/\s{2,}/g, ' ').trim();
};

export function filterColumns(columns, selectedColumnNames) {
  const filteredColumns = columns.filter((col) => {
    return selectedColumnNames.includes(col.header);
  })
  return filteredColumns;
};

export function orderColumns(columns, selectedColumnNames) {
  let orderedColumns = [];
  selectedColumnNames.forEach((columnName) => {
    orderedColumns.push(columns.filter(col => col.header === columnName)[0]);
  });
  return orderedColumns;
};

export function reorderArray(array, from, to) {
  const item = array.splice(from, 1);
  array.splice(to, 0, item[0]);
  return array;
};
