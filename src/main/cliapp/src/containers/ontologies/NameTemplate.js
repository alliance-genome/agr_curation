import React from 'react';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';

export const NameTemplate = ({ rowData }) => {
	return (
		<>
			<EllipsisTableCell otherClasses={`a${rowData.curie.replaceAll(':', '')}`}>{rowData.name}</EllipsisTableCell>
			<Tooltip target={`.a${rowData.curie.replaceAll(':', '')}`} content={rowData.name} style={{ width: '450px', maxWidth: '450px' }} />
		</>
	)
}
