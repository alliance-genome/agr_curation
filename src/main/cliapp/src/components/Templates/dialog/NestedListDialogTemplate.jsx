import { Button } from 'primereact/button';
import { ListTableCell } from '../../ListTableCell';

export const NestedListDialogTemplate = ({ entities, subType, handleOpen, getTextString, underline = true }) => {
	if (!entities || entities.length === 0 || !handleOpen || !subType || !getTextString) return null;

	const strings = entities.flatMap((entity) => entity[subType]?.map((item) => getTextString(item)) || []);
	const uniqueStrings = new Set(strings);
	const sortedStrings = Array.from(uniqueStrings).sort();

	const listTemplate = (item) => {
		return <span className={underline ? 'underline' : ''}>{item && item}</span>;
	};

	return (
		<>
			<div className={`-my-4 p-0`}>
				<Button className="p-button-text" onClick={() => handleOpen(entities)}>
					<ListTableCell template={listTemplate} listData={sortedStrings} />
				</Button>
			</div>
		</>
	);
};
