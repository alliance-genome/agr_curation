import { EllipsisTableCell } from '../EllipsisTableCell';
import { ListTableCell } from '../ListTableCell';
import { Tooltip } from 'primereact/tooltip';

export const EvidenceCodesTemplate = ({ evidenceCodes }) => {
	if (!evidenceCodes || evidenceCodes.length === 0) return null;

	const targetClass = `a${global.crypto.randomUUID()}`;

	const sortedEvidenceCodes = evidenceCodes.sort((a, b) =>
		a.abbreviation > b.abbreviation ? 1 : a.curie === b.curie ? 1 : -1
	);

	const listTemplate = (item) => {
		return <EllipsisTableCell>{item.abbreviation + ' - ' + item.name + ' (' + item.curie + ')'}</EllipsisTableCell>;
	};

	return (
		<>
			<div className={`-my-4 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={sortedEvidenceCodes} />
			</div>
			<Tooltip target={`.${targetClass}`} style={{ width: '450px', maxWidth: '450px' }} position="left">
				<ListTableCell template={listTemplate} listData={sortedEvidenceCodes} />
			</Tooltip>
		</>
	);
};
