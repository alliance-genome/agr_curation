import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';

export const BooleanTemplate = (value) => {
	console.log(value);
	return <EllipsisTableCell>{JSON.stringify(value)}</EllipsisTableCell>;
}
