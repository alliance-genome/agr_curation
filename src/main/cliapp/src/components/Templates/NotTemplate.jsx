import React from 'react';
import { EllipsisTableCell } from '../EllipsisTableCell';

export const NotTemplate = ({ value }) => {
	if (value === null || value === undefined || typeof value !== 'boolean') return null;
	const textString = value ? 'NOT' : '';
	return <EllipsisTableCell>{textString}</EllipsisTableCell>;
};
