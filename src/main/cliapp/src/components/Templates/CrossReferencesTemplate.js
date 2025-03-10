import { EllipsisTableCell } from '../EllipsisTableCell';
import { ListTableCell } from '../ListTableCell';
import { Tooltip } from 'primereact/tooltip';
import { crossReferencesSort } from '../../components/Templates/utils/sortMethods';

export const CrossReferencesTemplate = ({ list }) => {
	const normalizedList = Array.isArray(list) ? list : [list];

	const targetClass = `a${global.crypto.randomUUID()}`;

	const listTemplate = (item) => {
		if (!item) return null;
		return (
			<EllipsisTableCell>
				{' '}
				{item.displayName === item.referencedCurie
					? item.displayName
					: <> {item.displayName} <i> references </i> {item.referencedCurie} </>}{' '}
				({item.resourceDescriptorPage.name}){' '}
			</EllipsisTableCell>
		);
	};

	return (
		<>
			<div className={`-my-2 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={crossReferencesSort(normalizedList)} />
			</div>
			<Tooltip target={`.${targetClass}`} className="tooltip" mouseTrack position="bottom">
				<ListTableCell template={listTemplate} listData={crossReferencesSort(normalizedList)} />
			</Tooltip>
		</>
	);
};
