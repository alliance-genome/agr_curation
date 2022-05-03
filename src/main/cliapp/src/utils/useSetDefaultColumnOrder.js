import { useEffect } from "react";

export const useSetDefaultColumnOrder = (columns, dataTable, defaultColumnOptions) => {
  useEffect(() => {
    let initalColumnOrder = [];

    defaultColumnOptions.forEach((name) => {
      initalColumnOrder.push(
        columns.find((column) => {
          if (column.header === name) {
            return column.field;
          };
        })
      )
    });

    dataTable.current.state.columnOrder = initalColumnOrder;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};

