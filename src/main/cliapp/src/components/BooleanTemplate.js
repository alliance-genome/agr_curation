import React from 'react'
import { EllipsisTableCell } from './EllipsisTableCell';

export const BooleanTemplate = ({ value }) => {
	return <EllipsisTableCell>{JSON.stringify(value)}</EllipsisTableCell>;
}
