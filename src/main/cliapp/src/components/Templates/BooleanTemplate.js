import React from 'react'
import { EllipsisTableCell } from '../EllipsisTableCell';

export const BooleanTemplate = ({ value }) => {
	if (value === null || value === undefined) return null;
	return <EllipsisTableCell>{JSON.stringify(value)}</EllipsisTableCell>;
}
