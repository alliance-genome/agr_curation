import { EllipsisTableCell } from '../EllipsisTableCell';
import { ListTableCell } from '../ListTableCell';
import { Tooltip } from 'primereact/tooltip';

export const CrossReferencesTemplate = ({ xrefs }) => {
	if (!xrefs || xrefs.length === 0) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	const sortedXrefs = xrefs.sort((a, b) =>
		a.displayName > b.displayName ? 1 : a.resourceDescriptorPage.name === b.resourceDescriptorPage.name ? 1 : -1
	);

	const listTemplate = (item) => {
		return <EllipsisTableCell>{item.displayName + ' (' + item.resourceDescriptorPage.name + ')'}</EllipsisTableCell>;
	};

	return (
		<>
			<div className={`-my-2 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={sortedXrefs} />
			</div>
			<Tooltip target={`.${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position="left">
				<ListTableCell template={listTemplate} listData={sortedXrefs} />
			</Tooltip>
		</>
	);
};
