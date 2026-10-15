import { EllipsisTableCell } from '../../../components/EllipsisTableCell';
import { ListTableCell } from '../../../components/ListTableCell';
import { Tooltip } from 'primereact/tooltip';
import { strLabel } from '../utils';

/**
 * Read-only inline list of the STRs associated with an AGM, matching CrossReferencesTemplate's
 * convention for a system-managed association with no editor on the detail page.
 */
export const AssociatedSTRsTemplate = ({ list }) => {
	if (!list || list.length === 0) return null;

	const targetClass = `a${crypto.randomUUID()}`;

	const listTemplate = (association) => {
		const str = association?.agmSequenceTargetingReagentAssociationObject;
		if (!str) return null;
		return <EllipsisTableCell> {strLabel(str)} </EllipsisTableCell>;
	};

	return (
		<>
			<div className={`-my-2 p-1 ${targetClass}`}>
				<ListTableCell template={listTemplate} listData={list} />
			</div>
			<Tooltip target={`.${targetClass}`} className="tooltip" mouseTrack position="bottom">
				<ListTableCell template={listTemplate} listData={list} />
			</Tooltip>
		</>
	);
};
