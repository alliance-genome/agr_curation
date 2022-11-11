import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';

export const internalTemplate = (rowData) => {
	if (rowData && rowData.internal !== null && rowData.internal !== undefined) {
		return <EllipsisTableCell>{JSON.stringify(rowData.internal)}</EllipsisTableCell>;
	}
};

export const obsoleteTemplate = (rowData) => {
	if (rowData && rowData.obsolete !== null && rowData.obsolete !== undefined) {
		return <EllipsisTableCell>{JSON.stringify(rowData.obsolete)}</EllipsisTableCell>;
	}
};
