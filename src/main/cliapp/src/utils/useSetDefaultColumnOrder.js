import { useEffect } from "react";
import { setDefaultColumnOrder } from "./utils";

export const useSetDefaultColumnOrder = (columns, dataTable, defaultColumnOptions, setIsFirst, tableState, deletionEnabled=false) => {
	useEffect(() => {
		if (tableState.isFirst) {
			setDefaultColumnOrder(columns, dataTable, defaultColumnOptions, tableState, deletionEnabled);
			setIsFirst(false);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);
};

