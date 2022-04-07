import { useEffect } from "react";

export const useSetDefaultColumnOrder = (columns, dataTable) => {
  useEffect(() => {
    let initalColumnOrder = [];
    for (const column of columns) {
      initalColumnOrder.push(column.field);
    };
    dataTable.current.state.columnOrder = initalColumnOrder;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};

