import { useEffect } from "react";
import { setDefaultColumnOrder } from "./utils";

export const useSetDefaultColumnOrder = (columns, dataTable, defaultColumnOptions, setIsFirst, isFirst) => {
  useEffect(() => {
    if (isFirst) {
      setDefaultColumnOrder(columns, dataTable, defaultColumnOptions);
    };
    setIsFirst(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
};

