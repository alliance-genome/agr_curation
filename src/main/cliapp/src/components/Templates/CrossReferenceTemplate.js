import { EllipsisTableCell } from '../EllipsisTableCell';
import { Tooltip } from 'primereact/tooltip';

export const CrossReferenceTemplate = ({ xref }) => {
	if (!xref) return null;

	const targetClass = `x${global.crypto.randomUUID()}`;
	const xrefString = getXrefString(xref);
	return (
		<>
			<EllipsisTableCell otherClasses={targetClass}>{xrefString}</EllipsisTableCell>
			<Tooltip target={`.${targetClass}`} content={xrefString} mouseTrack position="bottom" />
		</>
	);
};

const getXrefString = (xref) => {
	return (
		<>
			{' '}
			{xref.displayName === xref.referencedCurie ? (
				xref.displayName
			) : (
				<>
					{' '}
					{xref.displayName} <i> references </i> {xref.referencedCurie}{' '}
				</>
			)}{' '}
			{xref.resourceDescriptorPage ? '(' + xref.resourceDescriptorPage.name + ')' : ''}{' '}
		</>
	);
};
