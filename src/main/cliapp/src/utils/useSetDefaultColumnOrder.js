import { useEffect } from "react";
import { setDefaultColumnOrder } from "./utils";

export const useSetDefaultColumnOrder = (columns, dataTable, defaultColumnOptions, setFirst, first) => {
  useEffect(() => {
    if (first) {
      setDefaultColumnOrder(columns, dataTable, defaultColumnOptions);
    };
    setFirst(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};

