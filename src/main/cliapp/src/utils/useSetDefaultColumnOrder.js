import { useEffect } from "react";
import { setDefaultColumnOrder } from "./utils";

export const useSetDefaultColumnOrder = (columns, dataTable, defaultColumnOptions, setIsFirst, isFirst, deletionEnabled=false, isLoading) => {
	useEffect(() => {
		if (isFirst) {
			setDefaultColumnOrder(columns, dataTable, defaultColumnOptions, deletionEnabled, isLoading);
			setIsFirst(false);
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);
};

