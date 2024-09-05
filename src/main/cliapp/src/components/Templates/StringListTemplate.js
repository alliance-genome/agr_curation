import { EllipsisTableCell } from '../EllipsisTableCell';
import { ListTableCell } from '../ListTableCell';
import { Tooltip } from 'primereact/tooltip';

export const StringListTemplate = ({ list }) => {
	if (!list || list.length === 0) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	//filter out falsy values
	const filteredList = list.filter((item) => item);
	const sortedList = filteredList.sort();

	const listTemplate = (item) => {
		if (!item) return;
		return <EllipsisTableCell>{item}</EllipsisTableCell>;
	};

	return (
		<>
			<div className={`-my-2 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={sortedList} />
			</div>
			<Tooltip target={`.${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position="left">
				<ListTableCell template={listTemplate} listData={sortedList} />
			</Tooltip>
		</>
	);
};
